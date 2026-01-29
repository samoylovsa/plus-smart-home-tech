package ru.yandex.practicum.warehouse.commerce.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Dimension {
    private Double width;
    private Double height;
    private Double depth;

    public Double getVolume() {
        return width * height * depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimension)) return false;
        Dimension that = (Dimension) o;
        return Objects.equals(width, that.width)
                && Objects.equals(height, that.height)
                && Objects.equals(depth, that.depth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth);
    }
}
