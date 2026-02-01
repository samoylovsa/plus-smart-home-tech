package ru.yandex.practicum.warehouse.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

import java.util.UUID;

public class ProductInShoppingCartLowQuantityInWarehouse extends ApiException {
    public ProductInShoppingCartLowQuantityInWarehouse(UUID productId, Long requested, Long available) {
        super(
                String.format("Product with id '%d' not enough in warehouse", productId),
                "Указанного продукта недостаточно на складе",
                HttpStatus.NOT_FOUND,
                "WAREHOUSE_PRODUCT_NOT_ENOUGH"
        );
    }
}