package ru.yandex.practicum.cart.commerce.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.commerce.service.ShoppingCartService;
import ru.yandex.practicum.interaction.api.commerce.client.shoppingCart.ShoppingCartApi;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartApi {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@NotBlank @RequestParam String username) {
        log.info("GET /api/v1/shopping-cart");
        ShoppingCartDto cart = shoppingCartService.getShoppingCart(username);
        log.info("Received shopping cart: {} for {}", cart, username);
        return ResponseEntity.ok(cart);
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @NotBlank @RequestParam String username,
            @RequestBody Map<UUID, Long> productQuantities) {
        log.info("PUT /api/v1/shopping-cart");
        ShoppingCartDto cart = shoppingCartService.addProductsToCart(username, productQuantities);
        log.info("Successfully added products to cart for user: {}", username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @NotBlank @RequestParam String username,
            @NotEmpty @RequestBody List<UUID> productIds) {
        log.info("POST /api/v1/shopping-cart/remove");
        ShoppingCartDto cart = shoppingCartService.removeProductsFromCart(username, productIds);
        log.info("Successfully removed {} products from cart for user: {}", productIds.size(), username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @NotBlank @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest request) {
        log.info("POST /api/v1/shopping-cart/change-quantity");
        ShoppingCartDto cart = shoppingCartService.changeProductQuantity(username, request);
        log.info("Successfully changed quantity for product {}", request.getProductId());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(@NotBlank @RequestParam String username) {
        log.info("DELETE /api/v1/shopping-cart");
        shoppingCartService.deactivateShoppingCart(username);
        log.info("Successfully deactivated shopping cart for user: {}", username);
        return ResponseEntity.ok().build();
    }
}
