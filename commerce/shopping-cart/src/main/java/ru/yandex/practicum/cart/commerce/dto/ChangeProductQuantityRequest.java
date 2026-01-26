package ru.yandex.practicum.cart.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {

    @NotNull
    @JsonProperty("productId")
    private UUID productId;

    @NotNull
    @JsonProperty("newQuantity")
    private Long newQuantity;
}
