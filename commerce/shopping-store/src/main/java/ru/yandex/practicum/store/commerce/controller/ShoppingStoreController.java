package ru.yandex.practicum.store.commerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.store.commerce.dto.PageResponseDto;
import ru.yandex.practicum.store.commerce.dto.ProductDto;
import ru.yandex.practicum.store.commerce.entity.enums.ProductCategory;
import ru.yandex.practicum.store.commerce.entity.enums.QuantityState;
import ru.yandex.practicum.store.commerce.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @PutMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("POST /api/v1/shopping-store body: {}", productDto);
        ProductDto createdProduct = shoppingStoreService.createProduct(productDto);
        log.info("Product created: {}", createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID productId) {
        log.info("GET /api/v1/shopping-store/{}", productId);
        ProductDto product = shoppingStoreService.getProduct(productId);
        log.info("Returning product:{}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public PageResponseDto<ProductDto> getProductsByCategory(
            @RequestParam ProductCategory category,
            Pageable pageable) {
        log.info("GET /api/v1/shopping-store?category={}, pageable: {}", category, pageable);
        Page<ProductDto> pageResult = shoppingStoreService.getProductsByCategory(category, pageable);
        log.info("Returning {} products for category: {}, page: {}, total pages: {}",
                pageResult.getNumberOfElements(), category, pageable.getPageNumber(), pageResult.getTotalPages());
        return PageResponseDto.fromPage(pageResult);
    }

    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store body: {}", productDto);
        ProductDto updatedProduct = shoppingStoreService.updateProduct(productDto);
        log.info("Product updated: {}", updatedProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProduct(@RequestBody UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore id: {}", productId);
        boolean result = shoppingStoreService.removeProduct(productId);
        log.info("Product removal result for id {}: {}", productId, result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState) {
        log.info("POST /api/v1/shopping-store/quantityState?productId={}&quantityState={}",
                productId, quantityState);
        boolean result = shoppingStoreService.setProductQuantityState(productId, quantityState);
        log.info("Quantity state update result for product {}: {}", productId, result);
        return ResponseEntity.ok(result);
    }
}
