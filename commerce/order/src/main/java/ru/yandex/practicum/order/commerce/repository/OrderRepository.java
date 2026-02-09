package ru.yandex.practicum.order.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.interaction.api.commerce.dto.order.OrderState;
import ru.yandex.practicum.order.commerce.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUsername(String username);

    List<Order> findByUsernameAndState(String username, OrderState state);

    Optional<Order> findByShoppingCartId(UUID shoppingCartId);
}