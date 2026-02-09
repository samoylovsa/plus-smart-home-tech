package ru.yandex.practicum.order.commerce.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.commerce.exception.ApiException;

public class NotAuthorizedUserException extends ApiException {

    public NotAuthorizedUserException() {
        super(
                "User is not authorized",
                "Имя пользователя не должно быть пустым",
                HttpStatus.UNAUTHORIZED,
                "ORDER_USER_UNAUTHORIZED"
        );
    }
}