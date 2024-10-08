package com.automation.tests.Quotes;

import com.automation.Utils.JsonUtil;
import com.automation.Utils.SessionUtil;
import com.automation.models.builders.RequestBuilder;
import com.automation.models.builders.ResponseBuilder;
import com.automation.models.pojo.Quotes.FavQuote.FavQuoteResponseBody;
import com.automation.models.pojo.Session.CreateUserSession.UserSessionResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.automation.enums.CategoryType;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.logging.Logger;

public class FavQuoteAPITest {
    private static final String FavQuote_EndPoint = "/quotes/{quote_id}/fav";
    private static final Logger log = Logger.getLogger(FavQuoteAPITest.class.getName());
    private static final int QUOTE_ID = 4;
    private static final String EXPECTED_RESPONSE_PATH = "src/test/resources/json/expectedFavQuotesAPITestData.json";
    private static final String EXPECTED_Body_KEY = "Body";
    private static final String EXPECTED_FavoritesCount_KEY = "FavoritesCount";
    private static final String EXPECTED_UpVotesCount_KEY = "UpVotesCount";
    private static final String EXPECTED_DownVotesCount_KEY = "DownVotesCount";
    private static final String EXPECTED_AuthorName_KEY = "AuthorName";
    private static final String EXPECTE_AuthorParamLink_KEY = "AuthorParamLink";



    private String sessionToken;

    @BeforeMethod(alwaysRun = true)
    public void createSession() {
        log.info("Starting session creation...");
        UserSessionResponseBody sessionResponse = SessionUtil.createSession();

        if (sessionResponse != null && sessionResponse.getUserToken() != null) {
            sessionToken = sessionResponse.getUserToken();
            log.info("Session created successfully with token: " + sessionToken);
        } else {
            log.severe("Failed to create session!");
            throw new IllegalStateException("Session creation failed.");
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
    private Response sendFavQuoteRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {

        return requestSpecification
                .pathParam("quote_id", QUOTE_ID)
                .header("User-Token", sessionToken)
                .when()
                .put(FavQuote_EndPoint)
                .then()
                .spec(responseSpecification).extract().response();
    }

    @Test(groups = {CategoryType.SMOKE_GROUP}, description = "Validates that the status code returned by the favQuote API is 200.")
    public void validateFavQuoteStatusCode() {
        log.info("Starting validateFavQuoteStatusCode test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending PUT request to favorite quote endpoint...");
        Response response = sendFavQuoteRequest(requestSpecification, responseSpecification);

        // Assert
        log.info("Starting all assertions for validateFavQuoteStatusCode test...");
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200, but found: " + response.statusCode());
        log.info("validateFavQuoteStatusCode test completed successfully.");
    }

    @Test(groups = {CategoryType.REGRESSION_GROUP, CategoryType.SANITY_GROUP}, description = "validate the ContentType of validateFavQuoteContentType")
    public void validateFavQuoteContentType() {
        log.info("Starting validateFavQuoteContentType test...");

        // Arrange
        RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
        ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

        // Act
        log.info("Sending PUT request to favorite quote endpoint...");
        Response response = sendFavQuoteRequest(requestSpecification, responseSpecification);

        // Assert
        log.info("Starting all assertions for validateFavQuoteContentType test...");
        Assert.assertEquals(response.getHeader("Content-Type"),"application/json; charset=utf-8","Unexpected content type.");
        log.info("validateFavQuoteContentType test completed successfully.");
    }

    @Test(groups = {CategoryType.REGRESSION_GROUP}, description = "validate the response json of validateFavQuoteResponseBody")
    public void validateFavQuoteResponseBody() {
        log.info("Starting validateFavQuoteResponseBody test...");

        try {
            // Arrange
            JsonNode jsonNode = JsonUtil.getJsonPathFromFile(EXPECTED_RESPONSE_PATH);

            RequestSpecification requestSpecification = RequestBuilder.createRequestSpecification();
            ResponseSpecification responseSpecification = ResponseBuilder.createResponseSpecification();

            // Act
            log.info("Sending PUT request to favorite quote endpoint...");
            Response response = sendFavQuoteRequest(requestSpecification, responseSpecification);

            // Convert response to POJO
            FavQuoteResponseBody favQuoteResponseBody = parseResponse(response);

            // Assert
            log.info("Starting all assertions for validateFavQuoteResponseBody test...");
            Assert.assertNotNull(favQuoteResponseBody, "FavQuoteResponseBody is null.");
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(favQuoteResponseBody.getId(), QUOTE_ID, "Quote ID mismatch");
            softAssert.assertFalse(favQuoteResponseBody.getDialogue(), "Unexpected dialogue value.");
            softAssert.assertFalse(favQuoteResponseBody.getPvate(), "Unexpected private value.");
            softAssert.assertEquals(favQuoteResponseBody.getBody(), jsonNode.path(EXPECTED_Body_KEY).asText());
            softAssert.assertEquals(favQuoteResponseBody.getFavorites_count(), jsonNode.path(EXPECTED_FavoritesCount_KEY).asInt());
            softAssert.assertEquals(favQuoteResponseBody.getUpvotes_count(), jsonNode.path(EXPECTED_UpVotesCount_KEY).asInt());
            softAssert.assertEquals(favQuoteResponseBody.getDownvotes_count(), jsonNode.path(EXPECTED_DownVotesCount_KEY).asInt());
            softAssert.assertEquals(favQuoteResponseBody.getAuthor(), jsonNode.path(EXPECTED_AuthorName_KEY).asText());
            softAssert.assertEquals(favQuoteResponseBody.getAuthor_permalink(), jsonNode.path(EXPECTE_AuthorParamLink_KEY).asText());

            // Assert all
            softAssert.assertAll();
            log.info("validateFavQuoteResponseBody test completed successfully.");
        }  catch (IOException e) {
            Assert.fail("Failed to read expected message from JSON file: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("Test failed due to an unexpected error: " + e.getMessage());
        }
    }

    private FavQuoteResponseBody parseResponse(Response response) {
        try {
            return new ObjectMapper().readValue(response.asString(), FavQuoteResponseBody.class);
        } catch (JsonProcessingException e) {
            log.severe("Failed to parse response body: " + e.getMessage());
            Assert.fail("JSON processing failed", e);
            return null;
        }
    }
}
