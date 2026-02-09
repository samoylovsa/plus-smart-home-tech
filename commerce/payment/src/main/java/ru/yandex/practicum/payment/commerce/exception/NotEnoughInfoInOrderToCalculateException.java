package ru.yandex.practicum.payment.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class NotEnoughInfoInOrderToCalculateException extends ApiException {

    public NotEnoughInfoInOrderToCalculateException(String message) {
        super(
                message,
                "Недостаточно информации в заказе для расчёта",
                HttpStatus.BAD_REQUEST,
                "PAYMENT_NOT_ENOUGH_INFO"
        );
    }
}
