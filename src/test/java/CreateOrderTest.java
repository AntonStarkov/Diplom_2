import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;

import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_LOGIN_USER;

public class CreateOrderTest {
    private StellarBurgersService stellarBurgersService;
    private static String accessToken;
    private static final String JSON_TO_CREATE_USER = "{\"email\": \"ninja@yandex.ru\", \"password\": \"ninja123\", \"name\": \"ninja\"}";
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
    public void createOrderWithAuthAndWithIngredients(){
        Assert.assertEquals(200, stellarBurgersService
                .createOrderResponseWithAuth(accessToken,
                        "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa7a\",\"61c0c5a71d1f82001bdaaa78\",\"61c0c5a71d1f82001bdaaa70\"]}")
                .getStatusCode());
    }
    @Test
    public void createOrderWithAuthAndWithoutIngredients(){
        Assert.assertEquals(400, stellarBurgersService
                .createOrderResponseWithAuth(accessToken, "{\"ingredients\": []}").getStatusCode());
    }
    @Test
    public void createOrderWithAuthAndWithIncorrectIngredientsHash(){
        Assert.assertEquals(500, stellarBurgersService
                .createOrderResponseWithAuth(accessToken, "{\"ingredients\": [\"incorrectHash\"]}").getStatusCode());
    }
    @Test
    public void createOrderWithoutAuthAndWithIngredients(){
        Assert.assertEquals(200, stellarBurgersService
                .createOrderResponseWithoutAuth("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa7a\",\"61c0c5a71d1f82001bdaaa78\",\"61c0c5a71d1f82001bdaaa70\"]}")
                .getStatusCode());
    }
    @Test
    public void createOrderWithoutAuthAndWithoutIngredients(){
        Assert.assertEquals(400, stellarBurgersService
                .createOrderResponseWithoutAuth("{\"ingredients\": []}").getStatusCode());
    }
    @Test
    public void createOrderWithoutAuthAndWithIncorrectIngredientsHash(){
        Assert.assertEquals(500, stellarBurgersService
                .createOrderResponseWithoutAuth("{\"ingredients\": [\"incorrectHash\"]}").getStatusCode());
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
