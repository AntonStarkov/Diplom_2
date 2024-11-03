import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderTest {
    private StellarBurgersService stellarBurgersService;
    private static String accessToken;
    private static Map<String, List<String>> mapIngredients;
    private static List<String> incorrectIngredient;
    @Rule
    public TestName testName = new TestName();
    @Before
    public void setUp() {
        RestAssured.requestSpecification = StellarBurgersService.requestSpec;
        stellarBurgersService = new StellarBurgersService();
        Map<String,String> userData = stellarBurgersService.userData();
        if(stellarBurgersService.loginUserResponse(userData).getStatusCode() == 200){
            LoginUserDeserialization loginUserDeserialization = stellarBurgersService.loginUserResponse(userData).as(LoginUserDeserialization.class);
            stellarBurgersService.deleteUserResponse(loginUserDeserialization.getAccessToken());
        }
        CreateUserDeserialization createUserDeserialization = stellarBurgersService.createUserResponse(userData).as(CreateUserDeserialization.class);
        accessToken = createUserDeserialization.getAccessToken();
    }
    @Test
    @DisplayName("Create order with authorization and with ingredients")
    @Description("Test for creating an order using authorization and ingredients. Using /api/orders")
    public void createOrderWithAuthAndWithIngredients(){
        mapIngredients = new HashMap<>(stellarBurgersService.takeIngredientsToMap());
        Assert.assertEquals(200, stellarBurgersService
                .createOrderResponseWithAuth(accessToken,
                        mapIngredients)
                .getStatusCode());
    }
    @Test
    @DisplayName("Create order with authorization and without ingredients")
    @Description("Test for creating an order using authorization and without ingredients. Using /api/orders")
    public void createOrderWithAuthAndWithoutIngredients(){
        mapIngredients = new HashMap<>();
        mapIngredients.put("ingredients", new ArrayList<>());
        Assert.assertEquals(400, stellarBurgersService
                .createOrderResponseWithAuth(accessToken, mapIngredients).getStatusCode());
    }
    @Test
    @DisplayName("Create order with authorization and with incorrect ingredient hash")
    @Description("Test for creating an order using authorization and incorrect hash of ingredient. Using /api/orders")
    public void createOrderWithAuthAndWithIncorrectIngredientHash(){
        mapIngredients = new HashMap<>();
        incorrectIngredient = new ArrayList<>();
        incorrectIngredient.add("incorrectHash");
        mapIngredients.put("ingredients", incorrectIngredient);
        Assert.assertEquals(500, stellarBurgersService
                .createOrderResponseWithAuth(accessToken, mapIngredients).getStatusCode());
    }
    @Test
    @DisplayName("Create order without authorization and with ingredients")
    @Description("Test for creating an order without authorization and using ingredients. Using /api/orders")
    public void createOrderWithoutAuthAndWithIngredients(){
        mapIngredients = new HashMap<>(stellarBurgersService.takeIngredientsToMap());
        Assert.assertEquals(200, stellarBurgersService
                .createOrderResponseWithoutAuth(mapIngredients)
                .getStatusCode());
    }
    @Test
    @DisplayName("Create order without authorization and without ingredients")
    @Description("Test for creating an order without authorization and ingredients. Using /api/orders")
    public void createOrderWithoutAuthAndWithoutIngredients(){
        mapIngredients = new HashMap<>();
        mapIngredients.put("ingredients", new ArrayList<>());
        Assert.assertEquals(400, stellarBurgersService
                .createOrderResponseWithoutAuth(mapIngredients).getStatusCode());
    }
    @Test
    @DisplayName("Create order without authorization and with incorrect ingredient hash")
    @Description("Test for creating an order without authorization and with incorrect hash of ingredient. Using /api/orders")
    public void createOrderWithoutAuthAndWithIncorrectIngredientsHash(){
        mapIngredients = new HashMap<>();
        incorrectIngredient = new ArrayList<>();
        incorrectIngredient.add("incorrectHash");
        mapIngredients.put("ingredients", incorrectIngredient);
        Assert.assertEquals(500, stellarBurgersService
                .createOrderResponseWithoutAuth(mapIngredients).getStatusCode());
    }
    @After
    public void deleteUser(){
        try {
            stellarBurgersService.deleteUserResponse(accessToken);
        } catch (NullPointerException e){
            System.out.println("User not created in" + " " + testName.getMethodName() + " method");
        }
    }
}
