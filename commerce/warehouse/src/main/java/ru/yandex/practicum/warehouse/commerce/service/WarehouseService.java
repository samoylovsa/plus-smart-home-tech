package ru.yandex.practicum.warehouse.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.commerce.dto.warehouse.*;
import ru.yandex.practicum.warehouse.commerce.entity.OrderDelivery;
import ru.yandex.practicum.warehouse.commerce.entity.WarehouseProduct;
import ru.yandex.practicum.warehouse.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.warehouse.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.commerce.mapper.WarehouseMapper;
import ru.yandex.practicum.warehouse.commerce.repository.OrderDeliveryRepository;
import ru.yandex.practicum.warehouse.commerce.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseProductRepository repository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final WarehouseMapper mapper;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        validateProductNotExists(request.getProductId());
        WarehouseProduct product = mapper.toEntity(request);
        repository.save(product);
    }

    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();
        WarehouseProduct product = findProductByIdOrThrow(productId);
        product.addQuantity(request.getQuantity());
        repository.save(product);
    }

    @Transactional
    public BookedProductsDto checkAndReserveProducts(ShoppingCartDto shoppingCart) {
        Map<UUID, Long> products = shoppingCart.getProducts();
        validateProductsAvailability(products);
        DeliveryCalculationResult calculation = calculateDeliveryDetails(products);
        return new BookedProductsDto(
                calculation.totalWeight(),
                calculation.totalVolume(),
                calculation.hasFragile()
        );
    }

    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Transactional
    public void registerDeliveryForOrder(UUID orderId, UUID deliveryId) {
        orderDeliveryRepository.findById(orderId)
                .ifPresentOrElse(
                        orderDelivery -> {
                            orderDelivery.setDeliveryId(deliveryId);
                            orderDeliveryRepository.save(orderDelivery);
                        },
                        () -> orderDeliveryRepository.save(OrderDelivery.builder()
                                .orderId(orderId)
                                .deliveryId(deliveryId)
                                .build())
                );
    }

    @Transactional
    public void acceptReturn(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            WarehouseProduct product = findProductByIdOrThrow(entry.getKey());
            product.addQuantity(entry.getValue());
            repository.save(product);
        }
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> products = request.getProducts();
        validateProductsAvailability(products);
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            WarehouseProduct product = findProductByIdOrThrow(entry.getKey());
            validateProductQuantity(product, entry.getValue());
            product.reduceQuantity(entry.getValue());
            repository.save(product);
        }
        orderDeliveryRepository.save(OrderDelivery.builder()
                .orderId(request.getOrderId())
                .deliveryId(null)
                .build());
        DeliveryCalculationResult calculation = calculateDeliveryDetails(products);
        return new BookedProductsDto(
                calculation.totalWeight(),
                calculation.totalVolume(),
                calculation.hasFragile()
        );
    }

    private void validateProductNotExists(UUID productId) {
        if (repository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(productId);
        }
    }

    private WarehouseProduct findProductByIdOrThrow(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));
    }

    private void validateProductsAvailability(Map<UUID, Long> products) {
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();
            WarehouseProduct product = findProductByIdOrThrow(productId);
            validateProductQuantity(product, requestedQuantity);
        }
    }

    private void validateProductQuantity(WarehouseProduct product, Long requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    product.getProductId(),
                    requestedQuantity,
                    product.getQuantity()
            );
        }
    }

    private DeliveryCalculationResult calculateDeliveryDetails(Map<UUID, Long> products) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            WarehouseProduct product = findProductByIdOrThrow(productId);
            totalWeight += calculateItemWeight(product, quantity);
            totalVolume += calculateItemVolume(product, quantity);
            if (product.getFragile()) {
                hasFragile = true;
            }
        }
        return new DeliveryCalculationResult(totalWeight, totalVolume, hasFragile);
    }

    private double calculateItemWeight(WarehouseProduct product, Long quantity) {
        return product.getWeight() * quantity;
    }

    private double calculateItemVolume(WarehouseProduct product, Long quantity) {
        return product.getDimension().getVolume() * quantity;
    }

    private record DeliveryCalculationResult(
            double totalWeight,
            double totalVolume,
            boolean hasFragile
    ) {}
}
