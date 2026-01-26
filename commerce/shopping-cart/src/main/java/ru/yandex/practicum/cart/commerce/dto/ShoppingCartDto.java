package ru.yandex.practicum.cart.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {

    @JsonProperty("shoppingCartId")
    private UUID shoppingCartId;

    @JsonProperty("products")
    private Map<UUID, Long> products;
}
