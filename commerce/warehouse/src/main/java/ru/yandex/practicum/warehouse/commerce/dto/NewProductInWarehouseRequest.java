package ru.yandex.practicum.warehouse.commerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(value = 1)
    private Double weight;
}
