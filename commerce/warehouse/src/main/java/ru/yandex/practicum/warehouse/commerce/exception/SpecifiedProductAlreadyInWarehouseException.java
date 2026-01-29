package ru.yandex.practicum.warehouse.commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super("Товар с ID " + productId + " уже зарегистрирован на складе");
    }
}
