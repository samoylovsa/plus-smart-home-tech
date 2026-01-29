package ru.yandex.practicum.warehouse.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    private Double width;
    private Double height;
    private Double depth;
}
