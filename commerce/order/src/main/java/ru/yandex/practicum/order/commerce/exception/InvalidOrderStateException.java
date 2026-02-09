package ru.yandex.practicum.order.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class InvalidOrderStateException extends ApiException {

    public InvalidOrderStateException(String message) {
        super(
                message,
                "Недопустимый переход статуса заказа",
                HttpStatus.BAD_REQUEST,
                "ORDER_INVALID_STATE"
        );
    }
}
