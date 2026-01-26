package ru.yandex.practicum.store.commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.store.commerce.dto.ProductDto;
import ru.yandex.practicum.store.commerce.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productId", source = "id")
    ProductDto toDto(Product entity);

    @Mapping(target = "id", source = "productId")
    Product toEntity(ProductDto dto);

    void updateEntity(@MappingTarget Product entity, ProductDto dto);
}
