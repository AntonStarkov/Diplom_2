package ru.yandex.praktikum.pojo.takeorder;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TakeOrderDeserialization {
    private boolean success;
    private List<Orders> orders;
    private int total;
    private int totalToday;
}
