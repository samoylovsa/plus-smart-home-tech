package ru.yandex.practicum.order.commerce.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.commerce.client.order.OrderApi;
import ru.yandex.practicum.interaction.api.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.commerce.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    @GetMapping
    public ResponseEntity<List<OrderDto>> getClientOrders(
            @NotBlank @RequestParam String username) {
        log.info("GET /api/v1/order");
        List<OrderDto> orders = orderService.getClientOrders(username);
        return ResponseEntity.ok(orders);
    }

    @Override
    @PutMapping
    public ResponseEntity<OrderDto> createNewOrder(
            @NotBlank @RequestParam String username,
            @Valid @RequestBody CreateNewOrderRequest request) {
        log.info("PUT /api/v1/order");
        OrderDto order = orderService.createNewOrder(username, request);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/return")
    public ResponseEntity<OrderDto> productReturn(
            @Valid @RequestBody ProductReturnRequest request) {
        log.info("POST /api/v1/order/return");
        OrderDto order = orderService.productReturn(request);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/payment")
    public ResponseEntity<OrderDto> payment(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/payment");
        OrderDto order = orderService.payment(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> paymentFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/payment/failed");
        OrderDto order = orderService.paymentFailed(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> delivery(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/delivery");
        OrderDto order = orderService.delivery(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> deliveryFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/delivery/failed");
        OrderDto order = orderService.deliveryFailed(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/completed")
    public ResponseEntity<OrderDto> complete(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/completed");
        OrderDto order = orderService.complete(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalCost(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/calculate/total");
        OrderDto order = orderService.calculateTotalCost(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryCost(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/calculate/delivery");
        OrderDto order = orderService.calculateDeliveryCost(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> assembly(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/assembly");
        OrderDto order = orderService.assembly(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> assemblyFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/assembly/failed");
        OrderDto order = orderService.assemblyFailed(orderId);
        return ResponseEntity.ok(order);
    }
}