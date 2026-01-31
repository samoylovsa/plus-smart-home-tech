package ru.yandex.practicum.interaction.api.commerce.exception.shoppingCart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<Map<String, Object>> handleNotAuthorizedUserException(
            NotAuthorizedUserException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("userMessage", "Имя пользователя не должно быть пустым");
        response.put("httpStatus", HttpStatus.UNAUTHORIZED.toString());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<Map<String, Object>> handleNoProductsInShoppingCartException(
            NoProductsInShoppingCartException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("userMessage", "Нет искомых товаров в корзине");
        response.put("httpStatus", HttpStatus.BAD_REQUEST.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
