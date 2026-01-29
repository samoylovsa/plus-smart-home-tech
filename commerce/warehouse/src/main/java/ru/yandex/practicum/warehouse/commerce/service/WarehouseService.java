package ru.yandex.practicum.warehouse.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.warehouse.commerce.dto.*;
import ru.yandex.practicum.warehouse.commerce.entity.Dimension;
import ru.yandex.practicum.warehouse.commerce.entity.WarehouseProduct;
import ru.yandex.practicum.warehouse.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.warehouse.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.commerce.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseProductRepository repository;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();

        if (repository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(productId);
        }

        Dimension dimension = new Dimension();
        dimension.setWidth(request.getDimension().getWidth());
        dimension.setHeight(request.getDimension().getHeight());
        dimension.setDepth(request.getDimension().getDepth());

        WarehouseProduct product = new WarehouseProduct();
        product.setProductId(productId);
        product.setFragile(request.getFragile() != null ? request.getFragile() : false);
        product.setDimension(dimension);
        product.setWeight(request.getWeight());
        product.setQuantity(0L);

        repository.save(product);
    }

    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();
        WarehouseProduct product = repository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));

        product.addQuantity(request.getQuantity());
        repository.save(product);
    }

    @Transactional
    public BookedProductsDto checkAndReserveProducts(ShoppingCartDto shoppingCart) {
        Map<UUID, Integer> products = shoppingCart.getProducts();

        // Проверяем доступность товаров
        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            WarehouseProduct product = repository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));

            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        productId, requestedQuantity, product.getQuantity()
                );
            }
        }

        // Рассчитываем характеристики доставки
        Double totalWeight = 0.0;
        Double totalVolume = 0.0;
        Boolean hasFragile = false;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey()).orElseThrow();

            totalWeight += product.getWeight() * entry.getValue();
            totalVolume += product.getDimension().getVolume() * entry.getValue();

            if (product.getFragile()) {
                hasFragile = true;
            }
        }

        return new BookedProductsDto(totalWeight, totalVolume, hasFragile);
    }

    public AddressDto getWarehouseAddress() {
        AddressDto address = new AddressDto();
        address.setCountry(CURRENT_ADDRESS);
        address.setCity(CURRENT_ADDRESS);
        address.setStreet(CURRENT_ADDRESS);
        address.setHouse(CURRENT_ADDRESS);
        address.setFlat(CURRENT_ADDRESS);
        return address;
    }
}
