package ru.yandex.practicum.payment.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class NoPaymentFoundException extends ApiException {

    public NoPaymentFoundException(UUID paymentId) {
        super(
                "Payment with id '" + paymentId + "' not found",
                "Платёж не найден",
                HttpStatus.NOT_FOUND,
                "PAYMENT_NOT_FOUND"
        );
    }
}
