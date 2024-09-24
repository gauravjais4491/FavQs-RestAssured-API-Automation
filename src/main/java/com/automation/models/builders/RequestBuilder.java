package com.automation.models.builders;

import static io.restassured.RestAssured.given;

import com.automation.Config.EnvironmentLoader;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASE_URI_KEY = "Base_URI";
    private static final String AUTH_TOKEN_KEY = "Authorization_Token";

    public static RequestSpecification createRequestSpecification() {
        String baseUri = EnvironmentLoader.getEnvVariable(BASE_URI_KEY);
        String authToken = EnvironmentLoader.getEnvVariable(AUTH_TOKEN_KEY);

        // Error handling for missing environment variables
        if (baseUri == null || authToken == null) {
            throw new IllegalArgumentException("Environment variables for base URI or authorization token are not set.");
        }

        return given()
                .baseUri(baseUri)
                .log().all()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header(AUTHORIZATION_HEADER, "Token token=" + authToken);
    }
}
