package ru.yandex.practicum.warehouse.commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouse(UUID productId, Long requested, Long available) {
        super("Product " + productId + ": requested " + requested + ", available " + available);
    }

    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super(message);
    }
}
