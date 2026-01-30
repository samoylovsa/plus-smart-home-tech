package ru.yandex.practicum.store.commerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.store.commerce.entity.Product;
import ru.yandex.practicum.interaction.api.commerce.dto.shoppingStore.ProductCategory;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByProductCategory(ProductCategory category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.productState = 'ACTIVE'")
    Optional<Product> findActiveById(@Param("id") UUID id);
}
