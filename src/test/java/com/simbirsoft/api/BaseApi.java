package com.simbirsoft.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApi {

    public static RequestSpecification getRequestSpecificationJson() {
        return RestAssured
            .given()
            .filter(new AllureRestAssured())
            .header("Content-Type", "application/json");
    }

    public static RequestSpecification getRequestSpecification() {
        return RestAssured
            .given()
            .filter(new AllureRestAssured());
    }
}
