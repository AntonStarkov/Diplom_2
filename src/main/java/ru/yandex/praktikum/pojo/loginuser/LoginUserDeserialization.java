package ru.yandex.praktikum.pojo.loginuser;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.praktikum.pojo.updateuser.User;

@Getter
@Setter
public class LoginUserDeserialization {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;

}
