package com.automation.tests.Session;

import com.automation.Config.EnvironmentLoader;
import com.automation.Utils.SessionUtil;
import com.automation.enums.CategoryType;
import com.automation.models.builders.ResponseBuilder;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionRequestBody;
import com.automation.models.pojo.Session.CreateUserSession.UserCredentialsRequestBody;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionResponseBody;
import com.automation.models.builders.RequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.logging.Logger;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_OK;


public class CreateUserSessionTest {
    private String login;
    private String email;
    private UserSessionRequestBody userSession;
    private static final String SESSION_ENDPOINT = "/session";
    private static Optional<String> sessionToken = Optional.empty();
    private static final Logger log = Logger.getLogger(CreateUserSessionTest.class.getName());
    private static final String loginKey = "login";
    private static final String passwordKey = "password";
    private static final String emailKey = "email";
    private static final String EXPECTED_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String content_Header_Key = "Content-Type";




    @BeforeClass(alwaysRun = true)
    public void setUp() {
        // Load environment variables
        login = EnvironmentLoader.getEnvVariable(loginKey);
        String password = EnvironmentLoader.getEnvVariable(passwordKey);
        email = EnvironmentLoader.getEnvVariable(emailKey);

        // Validate variables
        SessionUtil.validateEnvVariables(login, password, email);

        // Build user session request
        UserCredentialsRequestBody credential = SessionUtil.buildUserCredentials(login, password);
        userSession = new UserSessionRequestBody();
        userSession.setUser(credential);
    }
    private UserSessionResponseBody parseResponse(Response response) {
        try {
            return new ObjectMapper().readValue(response.asString(), UserSessionResponseBody.class);
        } catch (JsonProcessingException e) {
            log.severe("Failed to parse response body: " + e.getMessage());
            Assert.fail("JSON processing failed", e);
            return null;
        }
    }

    private Response sendCreateSessionRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return requestSpecification
                .body(userSession)
                .when()
                .post(SESSION_ENDPOINT)
                .then()
                .spec(responseSpecification)
                .extract().response();
    }


    @Test(groups = CategoryType.SMOKE_GROUP)
    public void validateCreateSessionStatusCode() {
        log.info("Starting validateCreateSessionStatusCode test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to create session endpoint...");
        Response response =  sendCreateSessionRequest(requestSpecification, responseSpecification);

        // Convert response to POJO
        UserSessionResponseBody userSessionResponseBody = parseResponse(response);
        Assert.assertNotNull(userSessionResponseBody, "CreateSessionResponseBody is null.");
        sessionToken = Optional.of(userSessionResponseBody.getUserToken());

        // Assertion
        Assert.assertEquals(response.statusCode(), HTTP_OK, "Expected status code is 200, but found: " + response.statusCode());
        log.info("validateCreateSessionStatusCode test completed successfully.");
    }

    @Test(groups = {CategoryType.SANITY_GROUP, CategoryType.REGRESSION_GROUP}, description = "validate the ContentType of validateCreateSessionContentType")
    public void validateCreateSessionContentType() {

        log.info("Starting validateCreateSessionContentType test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to create session endpoint...");
        Response response =  sendCreateSessionRequest(requestSpecification, responseSpecification);

        // Convert response to POJO
        UserSessionResponseBody userSessionResponseBody = parseResponse(response);
        Assert.assertNotNull(userSessionResponseBody, "CreateSessionResponseBody is null.");
        sessionToken = Optional.of(userSessionResponseBody.getUserToken());

        // Assertion
        Assert.assertEquals(response.getHeader(content_Header_Key),EXPECTED_CONTENT_TYPE,"Unexpected content type.");
        log.info("validateCreateSessionContentType test completed successfully.");
    }

    @Test(groups = {CategoryType.REGRESSION_GROUP}, description = "validate the response json of validateCreateSessionResponseBody")
    public void validateCreateSessionResponseBody() {

        log.info("Starting validateCreateSessionResponseBody test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to create session endpoint...");
        Response response =  sendCreateSessionRequest(requestSpecification, responseSpecification);

        // Convert response to POJO
        UserSessionResponseBody userSessionResponseBody = parseResponse(response);
        Assert.assertNotNull(userSessionResponseBody, "CreateSessionResponseBody is null.");
        sessionToken = Optional.of(userSessionResponseBody.getUserToken());

        // Assertion
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(userSessionResponseBody.getLogin(), login, "Login mismatch!");
        softAssert.assertEquals(userSessionResponseBody.getEmail(), email, "Email mismatch!");
        softAssert.assertNotNull(userSessionResponseBody.getUserToken(),  "User token is null! The session might not have been created properly.");

        // Assert all
        softAssert.assertAll();

        log.info("validateCreateSessionResponseBody test completed successfully.");
    }

    @AfterMethod(alwaysRun = true)
    public void destroySession() {
        log.info("Executing @AfterMethod - destroySession()");
        sessionToken.ifPresent(token -> {
            log.info("Destroying session...");
            SessionUtil.destroySession(token);
            log.info("Session destroyed successfully.");
        });
    }
}
