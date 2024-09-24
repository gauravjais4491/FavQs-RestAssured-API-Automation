package com.automation.models.builders;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

import static java.net.HttpURLConnection.HTTP_OK;

public final class ResponseBuilder {

    public static ResponseSpecification createResponseSpecification() {
        return new ResponseSpecBuilder().
                log(LogDetail.ALL).
                expectStatusCode(HTTP_OK).
                expectContentType(ContentType.JSON).build();
    }
}