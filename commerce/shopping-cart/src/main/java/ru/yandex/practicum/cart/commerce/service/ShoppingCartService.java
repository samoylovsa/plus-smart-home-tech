package ru.yandex.practicum.cart.commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.commerce.entity.ShoppingCart;
import ru.yandex.practicum.cart.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.cart.commerce.mapper.ShoppingCartMapper;
import ru.yandex.practicum.cart.commerce.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Корзина для пользователя '" + username + "' не найдена. " +
                                "Добавьте сначала товары в корзину."
                ));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> productQuantities) {
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
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена"));
        Map<UUID, Long> currentProducts = shoppingCart.getProducts();
        List<UUID> missingProducts = productIds.stream()
                .filter(productId -> !currentProducts.containsKey(productId))
                .toList();
        if (!missingProducts.isEmpty()) {
            throw new NoProductsInShoppingCartException(
                    "Нет искомых товаров в корзине: " + missingProducts
            );
        }
        productIds.forEach(currentProducts::remove);
        shoppingCart.setProducts(currentProducts);
        ShoppingCart savedCart = shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(savedCart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
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
        shoppingCartRepository.findByUsername(username)
                .ifPresent(cart -> {
                    cart.setActive(false);
                    shoppingCartRepository.save(cart);
                });
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUsername(username);
        newCart.setActive(true);
        newCart.setProducts(new HashMap<>());
        return shoppingCartRepository.save(newCart);
    }
}
