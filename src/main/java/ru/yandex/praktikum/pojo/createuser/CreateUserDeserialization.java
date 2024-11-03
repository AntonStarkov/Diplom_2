package ru.yandex.praktikum.pojo.createuser;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.praktikum.pojo.updateuser.User;

@Getter
@Setter
public class CreateUserDeserialization {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
}
