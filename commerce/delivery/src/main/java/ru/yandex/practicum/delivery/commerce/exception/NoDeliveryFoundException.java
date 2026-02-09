package ru.yandex.practicum.delivery.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class NoDeliveryFoundException extends ApiException {

    public NoDeliveryFoundException(UUID orderId) {
        super(
                "Delivery for order '" + orderId + "' not found",
                "Не найдена доставка",
                HttpStatus.NOT_FOUND,
                "NO_DELIVERY_FOUND"
        );
    }
}
