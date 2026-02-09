package ru.yandex.practicum.delivery.commerce.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.api.commerce.exception.GlobalExceptionHandler;

@RestControllerAdvice
public class DeliveryGlobalExceptionHandler extends GlobalExceptionHandler {
}
