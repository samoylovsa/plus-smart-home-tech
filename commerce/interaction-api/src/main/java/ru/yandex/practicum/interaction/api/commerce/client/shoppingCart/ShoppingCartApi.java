package ru.yandex.practicum.interaction.api.commerce.client.shoppingCart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartApi {

    @GetMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> getShoppingCart(@NotBlank @RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @NotBlank @RequestParam String username,
            @RequestBody Map<UUID, Long> productQuantities
    );

    @PostMapping("/api/v1/shopping-cart/remove")
    ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @NotBlank @RequestParam String username,
            @NotEmpty @RequestBody List<UUID> productIds
    );

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @NotBlank @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest request
    );

    @DeleteMapping("/api/v1/shopping-cart")
    ResponseEntity<Void> deactivateCurrentShoppingCart(@NotBlank @RequestParam String username);
}
