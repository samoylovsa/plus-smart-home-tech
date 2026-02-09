package ru.yandex.practicum.payment.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.commerce.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
