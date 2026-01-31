package ru.yandex.practicum.interaction.api.commerce.client.shoppingCart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.AddProductsRequest;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.ShoppingCartDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartApi {

    @GetMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam String username,
            @RequestBody AddProductsRequest request
    );

    @PostMapping("/api/v1/shopping-cart/remove")
    ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIds
    );

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest request
    );

    @DeleteMapping("/api/v1/shopping-cart")
    ResponseEntity<Void> deactivateCurrentShoppingCart(@RequestParam String username);
}
