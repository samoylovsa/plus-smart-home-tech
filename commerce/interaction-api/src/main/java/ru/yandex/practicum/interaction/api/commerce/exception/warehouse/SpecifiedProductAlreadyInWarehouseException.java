package ru.yandex.practicum.interaction.api.commerce.exception.warehouse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super("Product with ID " + productId + " is already in warehouse");
    }
}
