package ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductsRequest {

    @NotEmpty
    private Map<@NotNull UUID, @NotNull @Positive Long> productQuantities;
}
