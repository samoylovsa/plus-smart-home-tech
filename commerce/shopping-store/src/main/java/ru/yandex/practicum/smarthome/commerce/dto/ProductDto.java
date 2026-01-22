package ru.yandex.practicum.smarthome.commerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.smarthome.commerce.entity.enums.ProductCategory;
import ru.yandex.practicum.smarthome.commerce.entity.enums.ProductState;
import ru.yandex.practicum.smarthome.commerce.entity.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private UUID productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageSrc;

    @NotNull(message = "Quantity state is required")
    private QuantityState quantityState;

    @NotNull(message = "Product state is required")
    private ProductState productState;

    @NotNull(message = "Product category is required")
    private ProductCategory productCategory;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", message = "Price must be at least 1.0")
    private BigDecimal price;
}
