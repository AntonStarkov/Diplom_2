package ru.yandex.praktikum.pojo.createorder;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderDeserialization {
    private List<String> ingredients;
    private String name;
    private Order order;
    private boolean success;
}
