package ru.yandex.practicum.interaction.api.commerce.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private Map<UUID, Long> products;
}
