import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;

import java.util.Map;


public class CreateUserTest {
    private CreateUserDeserialization createUserDeserialization;
    private StellarBurgersService stellarBurgersService;
    private static Response response;
    private static Map<String,String> userData;
    @Rule
    public TestName testName = new TestName();
    @Before
    public void setUp() {
        stellarBurgersService = new StellarBurgersService();
        RestAssured.requestSpecification = StellarBurgersService.requestSpec;
        userData = stellarBurgersService.userData();
        if(stellarBurgersService.loginUserResponse(userData).getStatusCode() == 200){
            LoginUserDeserialization loginUserDeserialization = stellarBurgersService.loginUserResponse(userData).as(LoginUserDeserialization.class);
            stellarBurgersService.deleteUserResponse(loginUserDeserialization.getAccessToken());
        }
    }
    @Test
    @DisplayName("Create user")
    @Description("Test for creating user. Using /api/auth/register")
    public void createUser(){
        response = stellarBurgersService.createUserResponse(userData);
        Assert.assertEquals(200, response.getStatusCode());
        createUserDeserialization = response.as(CreateUserDeserialization.class);
    }
    @Test
    @DisplayName("Create user two times")
    @Description("Test for creating user using same request twice. Using /api/auth/register")
    public void createUserTwoTimes(){
        Response firstResponse = stellarBurgersService.createUserResponse(userData);
        Response secondResponse = stellarBurgersService.createUserResponse(userData);
        Assert.assertEquals(403, secondResponse.getStatusCode());
        createUserDeserialization = firstResponse.as(CreateUserDeserialization.class);
    }
    @Test
    @DisplayName("Create user without email")
    @Description("Test for creating user without email field in request. Using /api/auth/register")
    public void createUserWithoutEmail(){
        userData.remove("email");
        response = stellarBurgersService.createUserResponse(userData);
        Assert.assertEquals(403, response.getStatusCode());
        if(response.getStatusCode() == 200) {
            createUserDeserialization = response.as(CreateUserDeserialization.class);
        }
    }
    @Test
    @DisplayName("Create user without password")
    @Description("Test for creating user without password field in request. Using /api/auth/register")
    public void createUserWithoutPassword(){
        userData.remove("password");
        response = stellarBurgersService.createUserResponse(userData);
        Assert.assertEquals(403, response.getStatusCode());
        if(response.getStatusCode() == 200) {
            createUserDeserialization = response.as(CreateUserDeserialization.class);
        }
    }
    @Test
    @DisplayName("Create user without name")
    @Description("Test for creating user without name field in request. Using /api/auth/register")
    public void createUserWithoutName(){
        userData.remove("name");
        response = stellarBurgersService.createUserResponse(userData);
        Assert.assertEquals(403, response.getStatusCode());
        if(response.getStatusCode() == 200) {
            createUserDeserialization = response.as(CreateUserDeserialization.class);
        }
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
