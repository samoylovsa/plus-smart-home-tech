package ru.yandex.practicum.warehouse.commerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.warehouse.commerce.dto.*;
import ru.yandex.practicum.warehouse.commerce.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PutMapping
    public ResponseEntity<Void> newProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest request) {
        log.info("PUT /api/v1/warehouse");
        warehouseService.addNewProduct(request);
        log.info("New product: {} added to warehouse", request.getProductId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest request) {
        log.info("POST /api/v1/warehouse/add");
        warehouseService.addProductQuantity(request);
        log.info("Product: {} added to warehouse in quantity of: {}", request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(
            @Valid @RequestBody ShoppingCartDto shoppingCart) {
        log.info("POST /api/v1/warehouse/check");
        BookedProductsDto result = warehouseService.checkAndReserveProducts(shoppingCart);
        log.info("Products booked result: {} for shopping cart: {}", result, shoppingCart.getShoppingCartId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("GET /api/v1/warehouse/address");
        AddressDto address = warehouseService.getWarehouseAddress();
        log.info("Recieved address: {} for delivery", address);
        return ResponseEntity.ok(address);
    }
}
