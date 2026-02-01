package ru.yandex.practicum.cart.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class NoProductsInShoppingCartException extends ApiException {
    public NoProductsInShoppingCartException() {
        super(
                "No products in shopping cart",
                "В корзине нет товаров",
                HttpStatus.BAD_REQUEST,
                "NO_PRODUCTS_IN_SHOPPING_CART"
        );
    }
}
