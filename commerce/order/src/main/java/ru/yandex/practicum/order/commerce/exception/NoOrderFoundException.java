package ru.yandex.practicum.order.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class NoOrderFoundException extends ApiException {

    public NoOrderFoundException(UUID orderId) {
        super(
                String.format("Order with id '%s' not found", orderId),
                "Не найден заказ",
                HttpStatus.BAD_REQUEST,
                "ORDER_NOT_FOUND"
        );
    }
}