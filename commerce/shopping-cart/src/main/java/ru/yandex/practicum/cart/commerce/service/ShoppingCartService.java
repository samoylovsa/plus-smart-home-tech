package ru.yandex.practicum.cart.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.commerce.entity.ShoppingCart;
import ru.yandex.practicum.cart.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.cart.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.cart.commerce.mapper.ShoppingCartMapper;
import ru.yandex.practicum.cart.commerce.repository.ShoppingCartRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }

    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseGet(() -> createNewShoppingCart(username));

        return shoppingCartMapper.toDto(shoppingCart);
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUsername(username);
        newCart.setActive(true);
        newCart.setProducts(new HashMap<>());
        return shoppingCartRepository.save(newCart);
    }

    @Transactional
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> productQuantities) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseGet(() -> createNewShoppingCart(username));

        Map<UUID, Long> currentProducts = shoppingCart.getProducts();

        productQuantities.forEach((productId, quantity) -> {
            Long currentQuantity = currentProducts.getOrDefault(productId, 0L);
            currentProducts.put(productId, currentQuantity + quantity);
        });

        shoppingCart.setProducts(currentProducts);
        ShoppingCart savedCart = shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(savedCart);
    }

    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productIds) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена"));

        Map<UUID, Long> currentProducts = shoppingCart.getProducts();

        // Проверяем, что все товары есть в корзине
        List<UUID> missingProducts = productIds.stream()
                .filter(productId -> !currentProducts.containsKey(productId))
                .collect(Collectors.toList());

        if (!missingProducts.isEmpty()) {
            throw new NoProductsInShoppingCartException(
                    "Нет искомых товаров в корзине: " + missingProducts
            );
        }

        // Удаляем товары
        productIds.forEach(currentProducts::remove);

        shoppingCart.setProducts(currentProducts);
        ShoppingCart savedCart = shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(savedCart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена"));

        Map<UUID, Long> currentProducts = shoppingCart.getProducts();

        UUID productId = request.getProductId();
        Long newQuantity = request.getNewQuantity();

        if (!currentProducts.containsKey(productId)) {
            throw new NoProductsInShoppingCartException(
                    "Товар с ID " + productId + " не найден в корзине"
            );
        }

        if (newQuantity <= 0) {
            currentProducts.remove(productId);
        } else {
            currentProducts.put(productId, newQuantity);
        }

        shoppingCart.setProducts(currentProducts);
        ShoppingCart savedCart = shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(savedCart);
    }

    @Transactional
    public void deactivateShoppingCart(String username) {
        validateUsername(username);

        shoppingCartRepository.findByUsername(username)
                .ifPresent(cart -> {
                    cart.setActive(false);
                    shoppingCartRepository.save(cart);
                });
    }
}
