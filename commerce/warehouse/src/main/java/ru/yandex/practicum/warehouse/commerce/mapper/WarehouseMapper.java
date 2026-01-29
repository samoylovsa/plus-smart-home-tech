package ru.yandex.practicum.warehouse.commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.warehouse.commerce.dto.DimensionDto;
import ru.yandex.practicum.warehouse.commerce.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.commerce.entity.Dimension;
import ru.yandex.practicum.warehouse.commerce.entity.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    @Mapping(target = "quantity", constant = "0L")
    @Mapping(target = "fragile", source = "fragile", defaultValue = "false")
    @Mapping(target = "dimension", source = "dimension")
    WarehouseProduct toEntity(NewProductInWarehouseRequest request);

    Dimension toEntity(DimensionDto dto);
}
