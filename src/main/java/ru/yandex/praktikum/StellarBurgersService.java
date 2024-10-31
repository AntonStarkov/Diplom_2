package ru.yandex.praktikum;

import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static io.restassured.RestAssured.given;

public class StellarBurgersService {
    public static final String JSON_TO_CREATE_USER = "{\"email\": \"ninja@yandex.ru\", \"password\": \"ninja123\", \"name\": \"ninja\"}";
    public static final String JSON_TO_LOGIN_USER = "{\"email\": \"ninja@yandex.ru\", \"password\": \"ninja123\"}";
    @Step("Send POST request to create user to /api/auth/register")
    public Response createUserResponse(String jsonToCreateUser) {
        return given()
                        .header("Content-type", "application/json")
                        .body(jsonToCreateUser)
                        .post("/api/auth/register");
    }
    @Step("Send DELETE request to delete user to /api/auth/user")
    public void deleteUserResponse(@Param(mode=HIDDEN)String userToken){
               given()
                        .header("Authorization", userToken)
                        .delete("/api/auth/user")
                        .then()
                        .assertThat()
                        .statusCode(202);
    }
    @Step("Send POST request to login user to /api/auth/login")
    public Response loginUserResponse(String jsonToLoginUser){
        return given()
                        .header("Content-type", "application/json")
                        .body(jsonToLoginUser)
                        .post("/api/auth/login");
    }
    @Step("Send PATCH request to update user with auth to /api/auth/user")
    public Response updateUserResponseWithAuth(@Param(mode=HIDDEN)String userToken, String jsonToUpdate){
        return given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", userToken)
                        .body(jsonToUpdate)
                        .patch("/api/auth/user");
    }
    @Step("Send PATCH request to update user without auth to /api/auth/user")
    public Response updateUserResponseWithoutAuth(String jsonToUpdate){
        return given()
                        .header("Content-type", "application/json")
                        .body(jsonToUpdate)
                        .patch("/api/auth/user");
    }
    @Step("Send POST request to create order with auth to /api/orders")
    public Response createOrderResponseWithAuth(@Param(mode=HIDDEN)String userToken, String hashIngredientJson){
               return given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", userToken)
                        .body(hashIngredientJson)
                        .post("/api/orders");
    }
    @Step("Send POST request to create order without auth to /api/orders")
    public Response createOrderResponseWithoutAuth(String hashIngredientJson){
               return given()
                        .header("Content-type", "application/json")
                        .body(hashIngredientJson)
                        .post("/api/orders");
    }
    @Step("Send GET request to take order list with auth to /api/orders")
    public Response takeOrderResponseWithAuth(@Param(mode=HIDDEN)String userToken){
               return given()
                        .header("Authorization", userToken)
                        .get("/api/orders");
    }
    @Step("Send GET request to take order list without auth to /api/orders")
    public Response takeOrderResponseWithoutAuth(){
               return given()
                        .get("/api/orders");
    }
}
