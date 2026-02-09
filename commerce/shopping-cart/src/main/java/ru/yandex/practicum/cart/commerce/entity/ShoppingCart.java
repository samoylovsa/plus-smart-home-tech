package ru.yandex.practicum.cart.commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id", updatable = false, nullable = false)
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ElementCollection
    @CollectionTable(
            name = "cart_items",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();
}
