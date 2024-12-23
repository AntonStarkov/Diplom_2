import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.createorder.CreateOrderDeserialization;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;
import ru.yandex.praktikum.pojo.takeorder.Orders;
import ru.yandex.praktikum.pojo.takeorder.TakeOrderDeserialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeUsersOrderTest {
    private StellarBurgersService stellarBurgersService;
    private static String accessToken;
    @Rule
    public TestName testName = new TestName();
    @Before
    public void setUp() {
        RestAssured.requestSpecification = StellarBurgersService.requestSpec;
        stellarBurgersService = new StellarBurgersService();
        Map<String,String> userData = stellarBurgersService.userData();
        Map<String,String> userDataToLogin = new HashMap<>(userData);
        userDataToLogin.remove("name");
        if(stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode() == 200){
            LoginUserDeserialization loginUserDeserialization = stellarBurgersService.loginUserResponse(userDataToLogin).as(LoginUserDeserialization.class);
            stellarBurgersService.deleteUserResponse(loginUserDeserialization.getAccessToken());
        }
        CreateUserDeserialization createUserDeserialization = stellarBurgersService.createUserResponse(userData).as(CreateUserDeserialization.class);
        accessToken = createUserDeserialization.getAccessToken();
    }
    @Test
    @DisplayName("Take user orders")
    @Description("Test for take user orders using authorization in request. Using /api/orders")
    public void takeOrdersWithAuth(){
        List<Integer> numbersOfCreatedOrders = new ArrayList<>();
        List<Integer> numbersOfUserOrders = new ArrayList<>();
        Map<String, List<String>> mapIngredients = new HashMap<>(stellarBurgersService.takeIngredientsToMap());
        CreateOrderDeserialization createOrderDeserialization = stellarBurgersService
                .createOrderResponseWithAuth(accessToken,
                        mapIngredients).as(CreateOrderDeserialization.class);
        numbersOfCreatedOrders.add(createOrderDeserialization.getOrder().getNumber());
        mapIngredients.putAll(stellarBurgersService.takeIngredientsToMap());
        createOrderDeserialization = stellarBurgersService
                .createOrderResponseWithAuth(accessToken,
                        mapIngredients).as(CreateOrderDeserialization.class);
        numbersOfCreatedOrders.add(createOrderDeserialization.getOrder().getNumber());
        TakeOrderDeserialization takeOrderDeserialization = stellarBurgersService.takeOrderResponseWithAuth(accessToken).as(TakeOrderDeserialization.class);
        for(Orders order : takeOrderDeserialization.getOrders()){
            numbersOfUserOrders.add(order.getNumber());
        }
        Assert.assertEquals(numbersOfCreatedOrders, numbersOfUserOrders);
    }
    @Test
    @DisplayName("Take orders without authorization")
    @Description("Test for take orders without authorization in request. Using /api/orders")
    public void takeOrdersWithoutAuth(){
       Assert.assertEquals(401, stellarBurgersService.takeOrderResponseWithoutAuth().getStatusCode());
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
