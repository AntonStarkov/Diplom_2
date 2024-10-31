import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.updateuser.UpdateUserDeserialization;

import static ru.yandex.praktikum.StellarBurgersService.JSON_TO_LOGIN_USER;

public class UpdateUserTest {
    private UpdateUserDeserialization updateUserDeserialization;
    private LoginUserDeserialization loginUserDeserialization;
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
    public void updateUserEmailWithAuth(){
        updateUserDeserialization = stellarBurgersService.updateUserResponseWithAuth(accessToken, "{\"email\": \"ajnin@yandex.ru\"}")
                .as(UpdateUserDeserialization.class);
        Assert.assertEquals("ajnin@yandex.ru", updateUserDeserialization.getUser().getEmail());
    }
    @Test
    public void updateUserPasswordWithAuth(){
        stellarBurgersService.updateUserResponseWithAuth(accessToken, "{\"password\": \"ajnin321\"}");
        Assert.assertEquals(200, stellarBurgersService.loginUserResponse("{\"email\": \"ninja@yandex.ru\", \"password\": \"ajnin321\"}").getStatusCode());
    }
    @Test
    public void updateUserNameWithAuth(){
        updateUserDeserialization = stellarBurgersService.updateUserResponseWithAuth(accessToken, "{\"name\": \"ajnin\"}")
                .as(UpdateUserDeserialization.class);
        Assert.assertEquals("ajnin", updateUserDeserialization.getUser().getName());
    }
    @Test
    public void updateUserEmailWithoutAuth(){
        Assert.assertEquals(401, stellarBurgersService.updateUserResponseWithoutAuth("{\"email\": \"ajnin@yandex.ru\"}").getStatusCode());
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse("{\"email\": \"ajnin@yandex.ru\", \"password\": \"ninja123\"}").getStatusCode());
    }
    @Test
    public void updateUserPasswordWithoutAuth(){
        Assert.assertEquals(401, stellarBurgersService.updateUserResponseWithoutAuth("{\"password\": \"ajnin321\"}").getStatusCode());
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse("{\"email\": \"ninja@yandex.ru\", \"password\": \"ajnin321\"}").getStatusCode());
    }
    @Test
    public void updateUserNameWithoutAuth(){
        Assert.assertEquals(401, stellarBurgersService.updateUserResponseWithoutAuth("{\"name\": \"ajnin\"}").getStatusCode());
        loginUserDeserialization = stellarBurgersService.loginUserResponse("{\"email\": \"ninja@yandex.ru\", \"password\": \"ninja123\"}")
                .as(LoginUserDeserialization.class);
        Assert.assertEquals("ninja", loginUserDeserialization.getUser().getName());
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
