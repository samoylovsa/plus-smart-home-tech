package ru.yandex.practicum.payment.commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.interaction.api.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.commerce.entity.Payment;
import ru.yandex.practicum.payment.commerce.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { PaymentStatus.class }
)
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "status", expression = "java(PaymentStatus.PENDING)")
    Payment toEntity(UUID orderId, BigDecimal productCost, BigDecimal deliveryTotal,
                     BigDecimal feeTotal, BigDecimal totalPayment);
}
