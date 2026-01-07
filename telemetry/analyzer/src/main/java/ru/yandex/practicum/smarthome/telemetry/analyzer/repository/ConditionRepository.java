package ru.yandex.practicum.smarthome.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ConditionEntity;

@Repository
public interface ConditionRepository extends JpaRepository<ConditionEntity, Long> {

}
