package ru.yandex.praktikum.pojo.takeingredients;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class TakeIngredientsDeserialization {
    private List<Data> data;
    private boolean success;
}
