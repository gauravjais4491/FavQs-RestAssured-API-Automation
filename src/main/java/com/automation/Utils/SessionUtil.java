package com.automation.Utils;

import com.automation.Config.EnvironmentLoader;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionRequestBody;
import com.automation.models.pojo.Session.CreateUserSession.UserCredentialsRequestBody;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionResponseBody;
import com.automation.models.builders.RequestBuilder;
import com.automation.models.builders.ResponseBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class SessionUtil {
    private static final String SESSION_ENDPOINT = "/session";

    public static UserCredentialsRequestBody buildUserCredentials(String login, String password) {
        UserCredentialsRequestBody userCredentialsRequestBody = new UserCredentialsRequestBody();
        userCredentialsRequestBody.setLogin(login);
        userCredentialsRequestBody.setPassword(password);
        return userCredentialsRequestBody;
    }

    public static void validateEnvVariables(String login, String password, String email) {
        if (login == null || password == null || email == null) {
            throw new IllegalArgumentException("Missing environment variables for login, password, or email.");
        }
    }

    public static void validateLoginPassword(String login, String password) {
        if (login == null || password == null) {
            throw new IllegalArgumentException("Missing environment variables for login or password");
        }
    }
    public static UserSessionResponseBody createSession() {
        String login = EnvironmentLoader.getEnvVariable("login");
        String password = EnvironmentLoader.getEnvVariable("password");

        validateLoginPassword(login, password);

        // Build user session request
        UserCredentialsRequestBody credential = buildUserCredentials(login, password);
        UserSessionRequestBody userSession = new UserSessionRequestBody();
        userSession.setUser(credential);

        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        return requestSpecification
                .body(userSession)
                .when()
                .post(SESSION_ENDPOINT)
                .then()
                .spec(responseSpecification)
                .extract()
                .response()
                .as(UserSessionResponseBody.class);
    }

    public static void destroySession(String sessionToken) {
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        requestSpecification.header("User-Token", sessionToken)
                .when()
                .delete(SESSION_ENDPOINT)
                .then()
                .spec(responseSpecification)
                .assertThat()
                .statusCode(200); // Add custom status code validation if necessary
    }
}
