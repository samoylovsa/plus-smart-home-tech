package ru.yandex.practicum.smarthome.telemetry.analyzer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.SensorEntity;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<SensorEntity, String> {

    Optional<SensorEntity> findByIdAndHubId(String id, String hubId);
}
