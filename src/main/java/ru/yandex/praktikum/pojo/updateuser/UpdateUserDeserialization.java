package ru.yandex.praktikum.pojo.updateuser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDeserialization {
    private boolean success;
    private User user;
}
