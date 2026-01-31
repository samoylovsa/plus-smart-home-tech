package ru.yandex.practicum.interaction.api.commerce.exception.warehouse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super("No information about product " + productId + " in warehouse");
    }
}
