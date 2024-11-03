package ru.yandex.praktikum;

import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.datafaker.Faker;
import ru.yandex.praktikum.pojo.takeingredients.Data;
import ru.yandex.praktikum.pojo.takeingredients.TakeIngredientsDeserialization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static io.restassured.RestAssured.*;

public class StellarBurgersService {
    private static final String URI = "https://stellarburgers.nomoreparties.site";
    private static final String API_AUTH_REGISTER = "/api/auth/register";
    private static final String API_AUTH_USER = "/api/auth/user";
    private static final String API_AUTH_LOGIN = "/api/auth/login";
    private static final String API_ORDERS = "/api/orders";
    private static final String API_INGREDIENTS = "/api/ingredients";
    /*
    Cоздание лог файла в корне проекта для отслеживания сгенерированных данных при создания пользователя
    и возможности их удаления из базы в случае непредвиденных ошибок. Используется только в методе createUserResponse()
     */
    static PrintStream requestLog;
    static {
        try {
            requestLog = new PrintStream(new FileOutputStream("requestLog.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(URI)
            .addHeader("Content-type", "application/json")
            .build();
    @Step("Send POST request to create user to /api/auth/register")
    public Response createUserResponse(Map<String,String> mapToLoginUser) {
        return given()
                        .filter(RequestLoggingFilter.logRequestTo(requestLog))
                        .body(mapToLoginUser)
                        .post(API_AUTH_REGISTER);
    }
    @Step("Send DELETE request to delete user to /api/auth/user")
    public void deleteUserResponse(@Param(mode=HIDDEN)String userToken){
               given()
                        .header("Authorization", userToken)
                        .delete(API_AUTH_USER)
                        .then()
                        .assertThat()
                        .statusCode(202);
    }
    @Step("Send POST request to login user to /api/auth/login")
    public Response loginUserResponse(Map<String,String> mapToLoginUser){
        return given()
                        .body(mapToLoginUser)
                        .post(API_AUTH_LOGIN);
    }
    @Step("Send PATCH request to update user with auth to /api/auth/user")
    public Response updateUserResponseWithAuth(@Param(mode=HIDDEN)String userToken, Map<String,String> mapToUpdateUser){
        return given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", userToken)
                        .body(mapToUpdateUser)
                        .patch(API_AUTH_USER);
    }
    @Step("Send PATCH request to update user without auth to /api/auth/user")
    public Response updateUserResponseWithoutAuth(Map<String,String> mapToUpdateUser){
        return given()
                        .body(mapToUpdateUser)
                        .patch(API_AUTH_USER);
    }
    @Step("Send POST request to create order with auth to /api/orders")
    public Response createOrderResponseWithAuth(@Param(mode=HIDDEN)String userToken, Map<String, List<String>> mapToAddIngredients){
               return given()
                        .header("Authorization", userToken)
                        .body(mapToAddIngredients)
                        .post(API_ORDERS);
    }
    @Step("Send POST request to create order without auth to /api/orders")
    public Response createOrderResponseWithoutAuth(Map<String, List<String>> mapToAddIngredients){
               return given()
                        .body(mapToAddIngredients)
                        .post(API_ORDERS);
    }
    @Step("Send GET request to take order list with auth to /api/orders")
    public Response takeOrderResponseWithAuth(@Param(mode=HIDDEN)String userToken){
               return given()
                        .header("Authorization", userToken)
                        .get(API_ORDERS);
    }
    @Step("Send GET request to take order list without auth to /api/orders")
    public Response takeOrderResponseWithoutAuth(){
               return given()
                        .get(API_ORDERS);
    }
    @Step("Generate random email, password, name to create user")
    public Map<String,String> userData (){
        Faker faker = new Faker();
        Map<String,String> map = new HashMap<>();
        map.put("email", faker.internet().emailAddress());
        map.put("password", faker.internet().password(8, 16));
        map.put("name", faker.name().username());
        return map;
    }
    @Step("Send GET request to /api/ingredients to take ingredients and generate map with a set of random ingredients")
    public Map<String, List<String>> takeIngredientsToMap (){
        Random random = new Random();
        Map<String,List<String>> map = new HashMap<>();
        List<String> listIngredients = new ArrayList<>();
        List<String> listIngredientsHashBuns = new ArrayList<>();
        List<String> listIngredientsHashSauces = new ArrayList<>();
        List<String> listIngredientsHashFillings = new ArrayList<>();
        TakeIngredientsDeserialization takeIngredientsDeserialization =
                given()
                        .get(API_INGREDIENTS)
                        .as(TakeIngredientsDeserialization.class);
        for(Data data : takeIngredientsDeserialization.getData()) {
            if (data.getType().equals("bun")) {
                listIngredientsHashBuns.add(data.get_id());
            } else if (data.getType().equals("main")) {
                listIngredientsHashFillings.add(data.get_id());
            } else {
                listIngredientsHashSauces.add(data.get_id());
            }
        }
        listIngredients.add(listIngredientsHashBuns.get(random.nextInt(listIngredientsHashBuns.size())));
        listIngredients.add(listIngredientsHashSauces.get(random.nextInt(listIngredientsHashSauces.size())));
        listIngredients.add(listIngredientsHashFillings.get(random.nextInt(listIngredientsHashFillings.size())));
        map.put("ingredients", listIngredients);
        return map;
    }
}
