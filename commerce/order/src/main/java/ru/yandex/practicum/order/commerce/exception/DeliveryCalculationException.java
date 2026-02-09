package ru.yandex.practicum.order.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class DeliveryCalculationException extends ApiException {

    public DeliveryCalculationException(String message) {
        super(
                message,
                "Не удалось рассчитать стоимость доставки",
                HttpStatus.BAD_REQUEST,
                "ORDER_DELIVERY_CALCULATION_FAILED"
        );
    }
}
