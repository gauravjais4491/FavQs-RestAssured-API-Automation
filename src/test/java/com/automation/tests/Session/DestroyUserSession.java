package com.automation.tests.Session;
import com.automation.Utils.SessionUtil;
import com.automation.models.builders.RequestBuilder;
import com.automation.models.builders.ResponseBuilder;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionResponseBody;
import com.automation.models.pojo.Session.DestroyUserSession.DestroySessionResponseBody;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.automation.Utils.JsonUtil;

import java.io.IOException;
import java.util.logging.Logger;

public class DestroyUserSession {
    private static final String SESSION_ENDPOINT = "/session";
    private static final String EXPECTED_RESPONSE_PATH = "src/test/resources/json/expectedResponse.json";
    private static final String EXPECTED_MESSAGE_KEY = "message";
    private static final Logger log = Logger.getLogger(DestroyUserSession.class.getName());


    private static UserSessionResponseBody userSessionResponseBody;
    @BeforeMethod
    public void createSession() {
        log.info("Creating user session...");
        userSessionResponseBody = SessionUtil.createSession();
        if (userSessionResponseBody == null || userSessionResponseBody.getUserToken() == null) {
            throw new IllegalStateException("Failed to create session, user session or token is null.");
        }
        log.info("Session created successfully with token: " + userSessionResponseBody.getUserToken());
    }
    @Test
    public void destroySession() {

        try {
            // Arrange
            String sessionToken = userSessionResponseBody.getUserToken();
            String expectedMessage = JsonUtil.getValueFromJsonFile(EXPECTED_RESPONSE_PATH,EXPECTED_MESSAGE_KEY);

            RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
            ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

            // Act
            log.info("Destroying user session with token: " + sessionToken);
            DestroySessionResponseBody destroySessionResponseBody = requestSpecification.header("User-Token",sessionToken)
                    .when().delete(SESSION_ENDPOINT).then().spec(responseSpecification)
                    .extract().response().as(DestroySessionResponseBody.class);

            // Assert
            log.info("Asserting the response message...");
            Assert.assertEquals(destroySessionResponseBody.getMessage(),expectedMessage, "Message does not match!");
            log.info("Session destroyed successfully.");

        }  catch (IOException e) {
            Assert.fail("Failed to read expected message from JSON file: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("Test failed due to an unexpected error: " + e.getMessage());
        }
    }
}
