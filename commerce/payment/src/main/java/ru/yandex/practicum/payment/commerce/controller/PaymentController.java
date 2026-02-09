package ru.yandex.practicum.payment.commerce.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.commerce.client.payment.PaymentApi;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.commerce.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> payment(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/payment");
        PaymentDto dto = paymentService.payment(order);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/totalCost")
    public ResponseEntity<BigDecimal> getTotalCost(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/payment/totalCost");
        BigDecimal total = paymentService.getTotalCost(order);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/productCost")
    public ResponseEntity<BigDecimal> productCost(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/payment/productCost");
        BigDecimal cost = paymentService.productCost(order);
        return ResponseEntity.ok(cost);
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> paymentSuccess(@RequestBody @NotNull UUID paymentId) {
        log.info("POST /api/v1/payment/refund");
        paymentService.paymentSuccess(paymentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> paymentFailed(@RequestBody @NotNull UUID paymentId) {
        log.info("POST /api/v1/payment/failed");
        paymentService.paymentFailed(paymentId);
        return ResponseEntity.ok().build();
    }
}
