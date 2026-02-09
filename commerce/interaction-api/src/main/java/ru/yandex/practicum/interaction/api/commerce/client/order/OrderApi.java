package ru.yandex.practicum.interaction.api.commerce.client.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.commerce.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order")
public interface OrderApi {

    @GetMapping("/api/v1/order")
    ResponseEntity<List<OrderDto>> getClientOrders(@NotBlank @RequestParam String username);

    @PutMapping("/api/v1/order")
    ResponseEntity<OrderDto> createNewOrder(@NotBlank @RequestParam String username,
                                            @Valid @RequestBody CreateNewOrderRequest request);

    @PostMapping("/api/v1/order/return")
    ResponseEntity<OrderDto> productReturn(@Valid @RequestBody ProductReturnRequest request);

    @PostMapping("/api/v1/order/payment")
    ResponseEntity<OrderDto> payment(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    ResponseEntity<OrderDto> paymentFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery")
    ResponseEntity<OrderDto> delivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    ResponseEntity<OrderDto> deliveryFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/completed")
    ResponseEntity<OrderDto> complete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/total")
    ResponseEntity<OrderDto> calculateTotalCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/delivery")
    ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly")
    ResponseEntity<OrderDto> assembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    ResponseEntity<OrderDto> assemblyFailed(@RequestBody @NotNull UUID orderId);
}