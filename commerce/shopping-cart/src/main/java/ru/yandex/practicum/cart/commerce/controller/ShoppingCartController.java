package ru.yandex.practicum.cart.commerce.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.commerce.dto.AddProductsRequest;
import ru.yandex.practicum.cart.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.commerce.service.ShoppingCartService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam @NotBlank String username) {
        log.info("GET /api/v1/shopping-cart");
        ShoppingCartDto cart = shoppingCartService.getShoppingCart(username);
        log.info("Received shopping cart: {} for {}", cart, username);
        return ResponseEntity.ok(cart);
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam @NotBlank String username,
            @Valid @RequestBody AddProductsRequest request) {
        log.info("PUT /api/v1/shopping-cart");
        ShoppingCartDto cart = shoppingCartService.addProductsToCart(username, request.getProductQuantities());
        log.info("Successfully added products to cart for user: {}", username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam @NotBlank String username,
            @RequestBody @NotEmpty List<UUID> productIds) {
        log.info("POST /api/v1/shopping-cart/remove");
        ShoppingCartDto cart = shoppingCartService.removeProductsFromCart(username, productIds);
        log.info("Successfully removed {} products from cart for user: {}", productIds.size(), username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam @NotBlank String username,
            @Valid @RequestBody ChangeProductQuantityRequest request) {
        log.info("POST /api/v1/shopping-cart/change-quantity");
        ShoppingCartDto cart = shoppingCartService.changeProductQuantity(username, request);
        log.info("Successfully changed quantity for product {}", request.getProductId());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(@RequestParam @NotBlank String username) {
        log.info("DELETE /api/v1/shopping-cart");
        shoppingCartService.deactivateShoppingCart(username);
        log.info("Successfully deactivated shopping cart for user: {}", username);
        return ResponseEntity.ok().build();
    }
}
