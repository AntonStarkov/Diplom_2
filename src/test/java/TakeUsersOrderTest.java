import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.createorder.CreateOrderDeserialization;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;
import ru.yandex.praktikum.pojo.takeorder.TakeOrderDeserialization;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_CREATE_USER;
import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_LOGIN_USER;

public class TakeUsersOrderTest {
    private StellarBurgersService stellarBurgersService;
    private static String accessToken;
    @Rule
    public TestName testName = new TestName();
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        stellarBurgersService = new StellarBurgersService();
        if(stellarBurgersService.loginUserResponse(JSON_TO_LOGIN_USER).getStatusCode() == 200){
            LoginUserDeserialization loginUserDeserialization = stellarBurgersService.loginUserResponse(JSON_TO_LOGIN_USER).as(LoginUserDeserialization.class);
            stellarBurgersService.deleteUserResponse(loginUserDeserialization.getAccessToken());
        }
        CreateUserDeserialization createUserDeserialization = stellarBurgersService.createUserResponse(JSON_TO_CREATE_USER).as(CreateUserDeserialization.class);
        accessToken = createUserDeserialization.getAccessToken();
    }
    @Test
    public void takeOrdersWithAuth(){
        List<Integer> numbersOfCreatedOrders = new ArrayList<>();
        List<Integer> numbersOfUserOrders = new ArrayList<>();
        CreateOrderDeserialization createOrderDeserialization = stellarBurgersService
                .createOrderResponseWithAuth(accessToken,
                        "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa7a\",\"61c0c5a71d1f82001bdaaa78\",\"61c0c5a71d1f82001bdaaa70\"]}").as(CreateOrderDeserialization.class);
        numbersOfCreatedOrders.add(createOrderDeserialization.getOrder().getNumber());
        createOrderDeserialization = stellarBurgersService
                .createOrderResponseWithAuth(accessToken,
                        "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\",\"61c0c5a71d1f82001bdaaa72\"]}").as(CreateOrderDeserialization.class);
        numbersOfCreatedOrders.add(createOrderDeserialization.getOrder().getNumber());
        TakeOrderDeserialization takeOrderDeserialization = stellarBurgersService.takeOrderResponseWithAuth(accessToken).as(TakeOrderDeserialization.class);
        for(int i = 0; i < takeOrderDeserialization.getOrders().size(); i++){
            numbersOfUserOrders.add(takeOrderDeserialization.getOrders().get(i).getNumber());
        }
        Assert.assertEquals(numbersOfCreatedOrders, numbersOfUserOrders);
    }
    @Test
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
