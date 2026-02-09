package ru.yandex.practicum.order.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class NoSpecifiedProductInWarehouseException extends ApiException {

    public NoSpecifiedProductInWarehouseException() {
        super(
                "No specified product in warehouse for order",
                "Нет заказываемого товара на складе",
                HttpStatus.BAD_REQUEST,
                "ORDER_NO_PRODUCT_IN_WAREHOUSE"
        );
    }
}