package com.automation.tests.Session;

import com.automation.Config.EnvironmentLoader;
import com.automation.Utils.SessionUtil;
import com.automation.enums.CategoryType;
import com.automation.models.builders.ResponseBuilder;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionRequestBody;
import com.automation.models.pojo.Session.CreateUserSession.UserCredentialsRequestBody;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionResponseBody;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.automation.models.builders.RequestBuilder;
import java.util.logging.Logger;
import java.util.Optional;

public class CreateUserSessionTest {

    private static final String SESSION_ENDPOINT = "/session";
    private static Optional<String> sessionToken = Optional.empty();
    private static final Logger log = Logger.getLogger(CreateUserSessionTest.class.getName());

    @Test(groups = CategoryType.SMOKE_GROUP)
    public void createSession() {

        // Arrange
        String login = EnvironmentLoader.getEnvVariable("login");
        String password = EnvironmentLoader.getEnvVariable("password");
        String email = EnvironmentLoader.getEnvVariable("email");

        validateEnvVariables(login, password, email);

        UserCredentialsRequestBody credential = buildUserCredentials(login, password);
        UserSessionRequestBody userSession = new UserSessionRequestBody();
        userSession.setUser(credential);

        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Creating user session...");
        UserSessionResponseBody userSessionResponseBody =  requestSpecification
                .body(userSession).when().post(SESSION_ENDPOINT).then().spec(responseSpecification).extract().response().as(UserSessionResponseBody.class);

        log.info("User session created successfully.");
        sessionToken = userSessionResponseBody.getUserToken();

        // Assertion
        log.info("Validating the login returned in the response...");
        Assert.assertEquals(userSessionResponseBody.getLogin(), login, "Login mismatch!");
        log.info("Validating the email returned in the response...");
        Assert.assertEquals(userSessionResponseBody.getEmail(), email, "Email mismatch!");
        log.info("Checking if the user token is present in the response...");
        Assert.assertNotNull(userSessionResponseBody.getUserToken(),  "User token is null! The session might not have been created properly.");
        log.info("Session created and validated successfully.");
    }

    public UserCredentialsRequestBody buildUserCredentials(String login, String password) {
        UserCredentialsRequestBody userCredentialsRequestBody = new UserCredentialsRequestBody();
        userCredentialsRequestBody.setLogin(login);
        userCredentialsRequestBody.setPassword(password);
        return userCredentialsRequestBody;
    }

    public void validateEnvVariables(String login, String password, String email) {
        if (login == null || password == null || email == null) {
            throw new IllegalArgumentException("Missing environment variables for login, password, or email.");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void destroySession() {
        log.info("Executing @AfterMethod - destroySession()");
        if (sessionToken != null) {
            log.info("Destroying session...");
            SessionUtil.destroySession(sessionToken);
            log.info("Session destroyed successfully.");
        } else {
            log.warning("Session token is null, no session to destroy.");
        }
    }
}
