package ru.yandex.practicum.warehouse.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class NoSpecifiedProductInWarehouseException extends ApiException {
    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super(
                String.format("Product with id '%d' not found in warehouse", productId),
                "Указанный продукт не найден на складе",
                HttpStatus.NOT_FOUND,
                "WAREHOUSE_PRODUCT_NOT_FOUND"
        );
    }
}
