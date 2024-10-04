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
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_OK;

public class DestroyUserSessionTest {
    private static final String SESSION_ENDPOINT = "/session";
    private static final String EXPECTED_RESPONSE_PATH = "src/test/resources/json/expectedResponse.json";
    private static final String EXPECTED_MESSAGE_KEY = "message";
    private static final Logger log = Logger.getLogger(DestroyUserSessionTest.class.getName());
    private static Optional<String> sessionToken = Optional.empty();
    private static final String EXPECTED_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String content_Header_Key = "Content-Type";
    private static final String userToken_Header_Key = "User-Token";


    @BeforeMethod(alwaysRun = true)
    public void destroySession() {
        log.info("Creating user session...");
        UserSessionResponseBody userSessionResponseBody = SessionUtil.createSession();
        sessionToken = Optional.of(userSessionResponseBody.getUserToken());
        log.info("Session created successfully with token: " + userSessionResponseBody.getUserToken());
    }

    private Response sendDestroySessionRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return requestSpecification
                .header(userToken_Header_Key,sessionToken)
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
        log.info("Starting validateDestroySessionStatusCode test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to create session endpoint...");
        Response response =  sendDestroySessionRequest(requestSpecification, responseSpecification);

        // Assertion
        Assert.assertEquals(response.statusCode(), HTTP_OK, "Expected status code is 200, but found: " + response.statusCode());
        log.info("validateDestroySessionStatusCode test completed successfully.");
    }

    @Test(groups = {CategoryType.SANITY_GROUP, CategoryType.REGRESSION_GROUP}, description = "validate the ContentType of validateDestroySessionContentType")
    public void validateDestroySessionContentType() {

        log.info("Starting validateDestroySessionContentType test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending POST request to destroy session endpoint...");
        Response response =  sendDestroySessionRequest(requestSpecification, responseSpecification);

        // Assertion
        Assert.assertEquals(response.getHeader(content_Header_Key), EXPECTED_CONTENT_TYPE,"Unexpected content type.");
        log.info("validateDestroySessionContentType test completed successfully.");
    }

    @Test(groups = {CategoryType.REGRESSION_GROUP})
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
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertNotNull(destroySessionResponseBody, "DestroySessionResponseBody is null.");
            softAssert.assertEquals(destroySessionResponseBody.getMessage(),expectedMessage, "Message does not match!");

            //Assert All
            softAssert.assertAll();
            log.info("Session destroyed successfully.");

        }  catch (IOException e) {
            Assert.fail("Failed to read expected message from JSON file: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("Test failed due to an unexpected error: " + e.getMessage());
        }
    }
}
