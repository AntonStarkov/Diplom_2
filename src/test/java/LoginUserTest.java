import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;

import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_CREATE_USER;
import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_LOGIN_USER;

public class LoginUserTest {
    private CreateUserDeserialization createUserDeserialization;
    private StellarBurgersService stellarBurgersService;
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
        createUserDeserialization = stellarBurgersService.createUserResponse(JSON_TO_CREATE_USER).as(CreateUserDeserialization.class);
    }
    @Test
    public void userLogin(){
        Assert.assertEquals(200, stellarBurgersService.loginUserResponse("{\"email\": \"ninja@yandex.ru\", \"password\": \"ninja123\"}").getStatusCode());
    }
    @Test
    public void userLoginWithIncorrectLogin(){
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse("{\"email\": \"ajnin@yandex.ru\", \"password\": \"ninja123\"}").getStatusCode());
    }
    @Test
    public void userLoginWithIncorrectPassword(){
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse("{\"email\": \"ninja@yandex.ru\", \"password\": \"321ajnin\"}").getStatusCode());
    }
    @After
    public void deleteUser(){
        try {
            stellarBurgersService.deleteUserResponse(createUserDeserialization.getAccessToken());
        } catch (NullPointerException e){
            System.out.println("User not created in" + " " + testName.getMethodName() + " method");
        }
    }
}
