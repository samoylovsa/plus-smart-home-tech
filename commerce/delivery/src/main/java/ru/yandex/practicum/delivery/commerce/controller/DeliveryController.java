package ru.yandex.practicum.delivery.commerce.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.commerce.service.DeliveryService;
import ru.yandex.practicum.interaction.api.commerce.client.delivery.DeliveryApi;
import ru.yandex.practicum.interaction.api.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public ResponseEntity<DeliveryDto> planDelivery(@Valid @RequestBody DeliveryDto delivery) {
        log.info("PUT /api/v1/delivery");
        DeliveryDto created = deliveryService.planDelivery(delivery);
        return ResponseEntity.ok(created);
    }

    @Override
    @PostMapping("/cost")
    public ResponseEntity<BigDecimal> deliveryCost(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/delivery/cost");
        double cost = deliveryService.deliveryCost(order);
        return ResponseEntity.ok(BigDecimal.valueOf(cost));
    }

    @Override
    @PostMapping("/picked")
    public ResponseEntity<Void> deliveryPicked(@RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/delivery/picked");
        deliveryService.deliveryPicked(orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/successful")
    public ResponseEntity<Void> deliverySuccessful(@RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/delivery/successful");
        deliveryService.deliverySuccessful(orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/failed")
    public ResponseEntity<Void> deliveryFailed(@RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/delivery/failed");
        deliveryService.deliveryFailed(orderId);
        return ResponseEntity.ok().build();
    }
}
