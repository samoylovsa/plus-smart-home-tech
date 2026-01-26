package ru.yandex.practicum.cart.commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.cart.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.commerce.entity.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "products", source = "products")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "products", source = "products")
    ShoppingCart toEntity(ShoppingCartDto shoppingCartDto);
}
