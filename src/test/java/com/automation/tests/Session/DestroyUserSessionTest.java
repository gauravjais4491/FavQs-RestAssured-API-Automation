package com.automation.tests.Session;
import com.automation.Utils.SessionUtil;
import com.automation.enums.CategoryType;
import com.automation.models.builders.RequestBuilder;
import com.automation.models.builders.ResponseBuilder;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionResponseBody;
import com.automation.models.pojo.Session.DestroyUserSession.DestroySessionResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.automation.Utils.JsonUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class DestroyUserSessionTest {
    private static final String SESSION_ENDPOINT = "/session";
    private static final String EXPECTED_RESPONSE_PATH = "src/test/resources/json/expectedResponse.json";
    private static final String EXPECTED_MESSAGE_KEY = "message";
    private static final Logger log = Logger.getLogger(DestroyUserSessionTest.class.getName());
    private String sessionToken;


    @BeforeMethod(alwaysRun = true)
    public void destroySession() {
        log.info("Creating user session...");
        UserSessionResponseBody userSessionResponseBody = SessionUtil.createSession();
        sessionToken = userSessionResponseBody.getUserToken();
        if (sessionToken == null) {
            throw new IllegalStateException("Failed to create session, token is null.");
        }
        log.info("Session created successfully with token: " + userSessionResponseBody.getUserToken());
    }


    private Response sendDestroySessionRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return requestSpecification
                .header("User-Token",sessionToken)
                .when()
                .delete(SESSION_ENDPOINT)
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    private DestroySessionResponseBody parseResponse(Response response) {
        try {
            return new ObjectMapper().readValue(response.asString(), DestroySessionResponseBody.class);
        } catch (JsonProcessingException e) {
            log.severe("Failed to parse response body: " + e.getMessage());
            Assert.fail("JSON processing failed", e);
            return null;
        }
    }

    @Test(groups = CategoryType.SMOKE_GROUP)
    public void validateDestroySessionStatusCode() {
        log.info("Starting validateCreateSessionStatusCode test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to create session endpoint...");
        Response response =  sendDestroySessionRequest(requestSpecification, responseSpecification);

        // Assertion
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200, but found: " + response.statusCode());
        log.info("validateCreateSessionStatusCode test completed successfully.");
    }

    @Test(groups = {CategoryType.SANITY_GROUP, CategoryType.REGRESSION_GROUP}, description = "validate the ContentType of validateCreateSessionContentType")
    public void validateDestroySessionContentType() {

        log.info("Starting validateCreateSessionContentType test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to create session endpoint...");
        Response response =  sendDestroySessionRequest(requestSpecification, responseSpecification);

        // Assertion
        Assert.assertEquals(response.getHeader("Content-Type"),"application/json; charset=utf-8","Unexpected content type.");
        log.info("validateCreateSessionContentType test completed successfully.");
    }

    @Test(groups = {CategoryType.SMOKE_GROUP})
    public void validateDestroySessionResponseBody() {

        try {
            // Arrange
            String expectedMessage = JsonUtil.getValueFromJsonFile(EXPECTED_RESPONSE_PATH,EXPECTED_MESSAGE_KEY);
            RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
            ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

            // Act
            log.info("Destroying user session with token: " + sessionToken);
            Response response = sendDestroySessionRequest(requestSpecification, responseSpecification);

            // Convert Json to POJO
            DestroySessionResponseBody destroySessionResponseBody = parseResponse(response);

            // Assert
            Assert.assertNotNull(destroySessionResponseBody, "CreateSessionResponseBody is null.");
            Assert.assertEquals(destroySessionResponseBody.getMessage(),expectedMessage, "Message does not match!");
            log.info("Session destroyed successfully.");

        }  catch (IOException e) {
            Assert.fail("Failed to read expected message from JSON file: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("Test failed due to an unexpected error: " + e.getMessage());
        }
    }
}
