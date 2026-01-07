package ru.yandex.practicum.smarthome.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ScenarioEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Long> {

    List<ScenarioEntity> findByHubId(String hubId);

    Optional<ScenarioEntity> findByHubIdAndName(String hubId, String name);
}
