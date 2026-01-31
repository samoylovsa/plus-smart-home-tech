package ru.yandex.practicum.interaction.api.commerce.client.shoppingStore;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.PageResponseDto;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreApi {

    @PutMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ResponseEntity<ProductDto> getProduct(@PathVariable UUID productId);

    @GetMapping("/api/v1/shopping-store")
    PageResponseDto<ProductDto> getProductsByCategory(
            @RequestParam ProductCategory category,
            Pageable pageable
    );

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    ResponseEntity<Boolean> removeProduct(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState
    );
}
