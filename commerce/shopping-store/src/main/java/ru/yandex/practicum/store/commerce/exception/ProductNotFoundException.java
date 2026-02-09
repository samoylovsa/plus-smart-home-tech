package ru.yandex.practicum.store.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class ProductNotFoundException extends ApiException {
    public ProductNotFoundException(UUID productId) {
        super(
                String.format("Product with id '%d' not found", productId),
                "Продукт не найден",
                HttpStatus.NOT_FOUND,
                "STORE_PRODUCT_NOT_FOUND"
        );
    }
}
