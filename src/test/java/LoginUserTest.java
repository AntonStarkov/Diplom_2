import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;

import java.util.HashMap;
import java.util.Map;

public class LoginUserTest {
    private CreateUserDeserialization createUserDeserialization;
    private StellarBurgersService stellarBurgersService;
    private Map<String, String> userDataToLogin;
    @Rule
    public TestName testName = new TestName();
    @Before
    public void setUp() {
        RestAssured.requestSpecification = StellarBurgersService.requestSpec;
        stellarBurgersService = new StellarBurgersService();
        Map<String, String> userData = stellarBurgersService.userData();
        userDataToLogin = new HashMap<>(userData);
        userDataToLogin.remove("name");
        if(stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode() == 200){
            LoginUserDeserialization loginUserDeserialization = stellarBurgersService.loginUserResponse(userDataToLogin).as(LoginUserDeserialization.class);
            stellarBurgersService.deleteUserResponse(loginUserDeserialization.getAccessToken());
        }
        createUserDeserialization = stellarBurgersService.createUserResponse(userData).as(CreateUserDeserialization.class);
    }
    @Test
    @DisplayName("User authorization")
    @Description("Test for user authorization. Using /api/auth/login")
    public void userLogin(){
        Assert.assertEquals(200, stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode());
    }
    @Test
    @DisplayName("User authorization with incorrect login")
    @Description("Test for user authorization with incorrect login field in request. Using /api/auth/login")
    public void userLoginWithIncorrectLogin(){
        userDataToLogin.put("email", "ajnin@yandex.ru");
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode());
    }
    @Test
    @DisplayName("User authorization with incorrect password")
    @Description("Test for user authorization with incorrect password field in request. Using /api/auth/login")
    public void userLoginWithIncorrectPassword(){
        userDataToLogin.put("password", "321ajnin");
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode());
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
