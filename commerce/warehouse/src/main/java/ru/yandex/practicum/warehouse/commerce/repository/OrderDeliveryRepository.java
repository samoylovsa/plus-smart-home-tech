package ru.yandex.practicum.warehouse.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.commerce.entity.OrderDelivery;

import java.util.UUID;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, UUID> {
}
