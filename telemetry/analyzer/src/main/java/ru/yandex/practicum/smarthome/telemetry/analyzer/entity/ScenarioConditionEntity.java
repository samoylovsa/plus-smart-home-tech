package ru.yandex.practicum.smarthome.telemetry.analyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioConditionEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "scenario_id", nullable = false)
    private ScenarioEntity scenario;

    @Id
    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private SensorEntity sensor;

    @Id
    @ManyToOne
    @JoinColumn(name = "condition_id", nullable = false)
    private ConditionEntity condition;
}