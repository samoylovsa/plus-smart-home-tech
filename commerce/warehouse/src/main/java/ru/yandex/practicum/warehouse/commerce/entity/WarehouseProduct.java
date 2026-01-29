package ru.yandex.practicum.warehouse.commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarehouseProduct)) return false;
        return productId != null && productId.equals(((WarehouseProduct)o).productId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
