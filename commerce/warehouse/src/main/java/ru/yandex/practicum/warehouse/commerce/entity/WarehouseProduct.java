package ru.yandex.practicum.warehouse.commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProduct {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity = 0L;

    @Column(name = "fragile")
    private Boolean fragile = false;

    @Embedded
    private Dimension dimension;

    @Column(name = "weight")
    private Double weight;

    public void addQuantity(Long additionalQuantity) {
        this.quantity += additionalQuantity;
    }

    public void reduceQuantity(Long requestedQuantity) {
        this.quantity -= requestedQuantity;
    }
}
