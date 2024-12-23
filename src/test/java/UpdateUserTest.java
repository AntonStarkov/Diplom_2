import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.*;
import org.junit.rules.TestName;
import ru.yandex.praktikum.pojo.createuser.CreateUserDeserialization;
import ru.yandex.praktikum.pojo.loginuser.LoginUserDeserialization;
import ru.yandex.praktikum.StellarBurgersService;
import ru.yandex.praktikum.pojo.updateuser.UpdateUserDeserialization;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserTest {
    private UpdateUserDeserialization updateUserDeserialization;
    private LoginUserDeserialization loginUserDeserialization;
    private StellarBurgersService stellarBurgersService;
    private static String accessToken;
    private static Map<String,String> userDataToLogin;
    private static Map<String,String> userData;
    @Rule
    public TestName testName = new TestName();
    @Before
    public void setUp() {
        RestAssured.requestSpecification = StellarBurgersService.requestSpec;
        stellarBurgersService = new StellarBurgersService();
        userData = stellarBurgersService.userData();
        userDataToLogin = new HashMap<>(userData);
        userDataToLogin.remove("name");
        if(stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode() == 200){
            loginUserDeserialization = stellarBurgersService.loginUserResponse(userDataToLogin).as(LoginUserDeserialization.class);
            stellarBurgersService.deleteUserResponse(loginUserDeserialization.getAccessToken());
        }
        CreateUserDeserialization createUserDeserialization = stellarBurgersService.createUserResponse(userData).as(CreateUserDeserialization.class);
        accessToken = createUserDeserialization.getAccessToken();
    }
    @Test
    @DisplayName("Update user email with authorization")
    @Description("Test for update user email field using authorization in request. Using /api/auth/user")
    public void updateUserEmailWithAuth(){
        userData.remove("password");
        userData.remove("name");
        userData.put("email", new StringBuilder(userData.get("email")).reverse().toString());
        updateUserDeserialization = stellarBurgersService.updateUserResponseWithAuth(accessToken, userData)
                .as(UpdateUserDeserialization.class);
        Assert.assertEquals(userData.get("email"), updateUserDeserialization.getUser().getEmail());
    }
    @Test
    @DisplayName("Update user password with authorization")
    @Description("Test for update user password field using authorization in request. Using /api/auth/user")
    public void updateUserPasswordWithAuth(){
        userData.remove("email");
        userData.remove("name");
        userData.put("password", new StringBuilder(userData.get("password")).reverse().toString());
        stellarBurgersService.updateUserResponseWithAuth(accessToken, userData);
        userDataToLogin.put("password", new StringBuilder(userDataToLogin.get("password")).reverse().toString());
        Assert.assertEquals(200, stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode());
    }
    @Test
    @DisplayName("Update user name with authorization")
    @Description("Test for update user name field using authorization in request. Using /api/auth/user")
    public void updateUserNameWithAuth(){
        userData.remove("email");
        userData.remove("password");
        userData.put("name", new StringBuilder(userData.get("name")).reverse().toString());
        updateUserDeserialization = stellarBurgersService.updateUserResponseWithAuth(accessToken, userData)
                .as(UpdateUserDeserialization.class);
        Assert.assertEquals(userData.get("name"), updateUserDeserialization.getUser().getName());
    }
    @Test
    @DisplayName("Update user email without authorization")
    @Description("Test for update user email field without authorization in request. Using /api/auth/user")
    public void updateUserEmailWithoutAuth(){
        userData.remove("password");
        userData.remove("name");
        userData.put("email", new StringBuilder(userData.get("email")).reverse().toString());
        Assert.assertEquals(401, stellarBurgersService.updateUserResponseWithoutAuth(userData).getStatusCode());
        userDataToLogin.put("email", userData.get("email"));
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode());
    }
    @Test
    @DisplayName("Update user password without authorization")
    @Description("Test for update user password field without authorization in request. Using /api/auth/user")
    public void updateUserPasswordWithoutAuth(){
        userData.remove("email");
        userData.remove("name");
        userData.put("password", new StringBuilder(userData.get("password")).reverse().toString());
        Assert.assertEquals(401, stellarBurgersService.updateUserResponseWithoutAuth(userData).getStatusCode());
        userDataToLogin.put("password", userData.get("password"));
        Assert.assertEquals(401, stellarBurgersService.loginUserResponse(userDataToLogin).getStatusCode());
    }
    @Test
    @DisplayName("Update user name without authorization")
    @Description("Test for update user name field without authorization in request. Using /api/auth/user")
    public void updateUserNameWithoutAuth(){
        userData.remove("email");
        userData.remove("password");
        userData.put("name", new StringBuilder(userData.get("name")).reverse().toString());
        Assert.assertEquals(401, stellarBurgersService.updateUserResponseWithoutAuth(userData).getStatusCode());
        loginUserDeserialization = stellarBurgersService.loginUserResponse(userDataToLogin)
                .as(LoginUserDeserialization.class);
        Assert.assertEquals(new StringBuilder(userData.get("name")).reverse().toString(), loginUserDeserialization.getUser().getName());
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
