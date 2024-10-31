import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;

import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_LOGIN_USER;


public class CreateUserTest {
    private CreateUserDeserialization createUserDeserialization;
    private StellarBurgersService stellarBurgersService;
    private static Response response;
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
    }
    @Test
    public void createUser(){
        response = stellarBurgersService.createUserResponse(JSON_TO_CREATE_USER);
        Assert.assertEquals(200, response.getStatusCode());
        createUserDeserialization = response.as(CreateUserDeserialization.class);
    }
    @Test
    public void createUserTwoTimes(){
        Response firstResponse = stellarBurgersService.createUserResponse(JSON_TO_CREATE_USER);
        Response secondResponse = stellarBurgersService.createUserResponse(JSON_TO_CREATE_USER);
        Assert.assertEquals(403, secondResponse.getStatusCode());
        createUserDeserialization = firstResponse.as(CreateUserDeserialization.class);
    }
    @Test
    public void createUserWithoutEmail(){
        response = stellarBurgersService.createUserResponse("{\"password\": \"ninja123\", \"name\": \"ninja\"}");
        Assert.assertEquals(403, response.getStatusCode());
        if(response.getStatusCode() == 200) {
            createUserDeserialization = response.as(CreateUserDeserialization.class);
        }
    }
    @Test
    public void createUserWithoutPassword(){
        response = stellarBurgersService.createUserResponse("{\"email\": \"ninja@yandex.ru\", \"name\": \"ninja\"}");
        Assert.assertEquals(403, response.getStatusCode());
        if(response.getStatusCode() == 200) {
            createUserDeserialization = response.as(CreateUserDeserialization.class);
        }
    }
    @Test
    public void createUserWithoutName(){
        response = stellarBurgersService.createUserResponse("{\"email\": \"ninja@yandex.ru\", \"password\": \"ninja123\"}");
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
