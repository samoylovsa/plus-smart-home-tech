package ru.yandex.practicum.warehouse.commerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToWarehouseRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Long quantity;
}
