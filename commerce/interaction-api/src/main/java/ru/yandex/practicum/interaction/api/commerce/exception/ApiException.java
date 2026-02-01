package ru.yandex.practicum.interaction.api.commerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;
    private final String errorCode;

    protected ApiException(String message, String userMessage,
                           HttpStatus httpStatus, String errorCode) {
        super(message);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
