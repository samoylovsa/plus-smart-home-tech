package ru.yandex.practicum.store.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.ProductState;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_src")
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    @Builder.Default
    private ProductState productState = ProductState.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
