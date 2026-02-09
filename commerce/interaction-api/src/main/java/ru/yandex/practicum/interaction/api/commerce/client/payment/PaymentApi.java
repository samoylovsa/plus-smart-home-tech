package ru.yandex.practicum.interaction.api.commerce.client.payment;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment")
public interface PaymentApi {

    @PostMapping("/api/v1/payment")
    ResponseEntity<PaymentDto> payment(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/totalCost")
    ResponseEntity<BigDecimal> getTotalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/productCost")
    ResponseEntity<BigDecimal> productCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/refund")
    ResponseEntity<Void> paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/failed")
    ResponseEntity<Void> paymentFailed(@RequestBody UUID paymentId);
}
