package ru.yandex.practicum.store.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.store.commerce.dto.ProductDto;
import ru.yandex.practicum.store.commerce.entity.Product;
import ru.yandex.practicum.store.commerce.entity.enums.ProductCategory;
import ru.yandex.practicum.store.commerce.entity.enums.ProductState;
import ru.yandex.practicum.store.commerce.entity.enums.QuantityState;
import ru.yandex.practicum.store.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.store.commerce.mapper.ProductMapper;
import ru.yandex.practicum.store.commerce.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ProductDto getProduct(UUID productId) {
        Product product = findProductOrThrow(productId);
        return productMapper.toDto(product);
    }

    public Page<ProductDto> getProductsByCategory(
            ProductCategory category,
            Pageable pageable) {
        Page<Product> products = productRepository.findAllByProductCategory(category, pageable);
        return products.map(productMapper::toDto);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required for update");
        }
        Product existingProduct = findProductOrThrow(productDto.getProductId());
        productMapper.updateEntity(existingProduct, productDto);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public boolean removeProduct(UUID productId) {
        Product product = findActiveProductOrThrow(productId);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        Product product = findProductOrThrow(productId);
        product.setQuantityState(quantityState);
        productRepository.save(product);
        return true;
    }

    private Product findActiveProductOrThrow(UUID productId) {
        return productRepository.findActiveById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Active product not found with ID: " + productId));
    }

    private Product findProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }
}
