package ru.yandex.practicum.warehouse.commerce.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimension {
    private Double width;
    private Double height;
    private Double depth;

    public Double getVolume() {
        return width * height * depth;
    }
}
