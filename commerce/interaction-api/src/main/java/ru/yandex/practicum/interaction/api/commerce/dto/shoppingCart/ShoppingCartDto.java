package ru.yandex.practicum.interaction.api.commerce.dto.shoppingCart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {

    private UUID shoppingCartId;

    private Map<UUID, Long> products;
}
