package ru.yandex.practicum.cart.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class NotAuthorizedUserException extends ApiException {
    public NotAuthorizedUserException() {
        super(
                "User is not authorized",
                "Имя пользователя не должно быть пустым",
                HttpStatus.UNAUTHORIZED,
                "CART_USER_UNAUTHORIZED"
        );
    }
}
