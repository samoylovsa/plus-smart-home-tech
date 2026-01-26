package ru.yandex.practicum.cart.commerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.commerce.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(
            @RequestParam String username) {
        ShoppingCartDto cart = shoppingCartService.getShoppingCart(username);
        return ResponseEntity.ok(cart);
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Long> productQuantities) {
        ShoppingCartDto cart = shoppingCartService.addProductsToCart(username, productQuantities);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIds) {
        ShoppingCartDto cart = shoppingCartService.removeProductsFromCart(username, productIds);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest request) {
        ShoppingCartDto cart = shoppingCartService.changeProductQuantity(username, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(
            @RequestParam String username) {
        shoppingCartService.deactivateShoppingCart(username);
        return ResponseEntity.ok().build();
    }
}
