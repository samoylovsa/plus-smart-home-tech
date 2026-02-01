package ru.yandex.practicum.warehouse.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class SpecifiedProductAlreadyInWarehouseException extends ApiException {
    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super(
                String.format("Product '%s' already exists in warehouse", productId),
                "Данный продукт уже существует на складе",
                HttpStatus.BAD_REQUEST,
                "WAREHOUSE_PRODUCT_ALREADY_EXISTS"
        );
    }
}
