package ru.yandex.practicum.interaction.api.commerce.client.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery")
public interface DeliveryApi {

    @PutMapping("/api/v1/delivery")
    ResponseEntity<DeliveryDto> planDelivery(@Valid @RequestBody DeliveryDto delivery);

    @PostMapping("/api/v1/delivery/cost")
    ResponseEntity<BigDecimal> deliveryCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/delivery/picked")
    ResponseEntity<Void> deliveryPicked(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/delivery/successful")
    ResponseEntity<Void> deliverySuccessful(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    ResponseEntity<Void> deliveryFailed(@RequestBody @NotNull UUID orderId);
}
