package ru.yandex.practicum.smarthome.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.*;
import ru.yandex.practicum.smarthome.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.smarthome.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.smarthome.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.smarthome.telemetry.analyzer.repository.SensorRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    public void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        String scenarioName = event.getName();
        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresentOrElse(
                        existing -> updateScenario(existing, hubId, event),
                        () -> createNewScenario(hubId, event)
                );
    }

    public void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        String scenarioName = event.getName();
        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresent(scenarioRepository::delete);
    }

    public void handleDeviceAdded(String hubId, DeviceAddedEventAvro event) {
        String sensorId = event.getId();
        sensorRepository.findById(sensorId)
                .ifPresentOrElse(
                        existing -> {
                            if (!existing.getHubId().equals(hubId)) {
                                existing.setHubId(hubId);
                                sensorRepository.save(existing);
                                log.debug("Updated hub for sensor {} to {}", sensorId, hubId);
                            }
                        },
                        () -> {
                            SensorEntity sensor = new SensorEntity(sensorId, hubId);
                            sensorRepository.save(sensor);
                            log.debug("Created sensor {} for hub {}", sensorId, hubId);
                        }
                );
    }

    public void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
        String sensorId = event.getId();
        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .ifPresent(sensor -> {
                    List<ScenarioEntity> scenarios = scenarioRepository.findByHubId(hubId);
                    scenarios.forEach(scenario -> {
                        scenario.getConditions().removeIf(cond ->
                                cond.getSensor().getId().equals(sensorId));
                        scenario.getActions().removeIf(action ->
                                action.getSensor().getId().equals(sensorId));
                    });
                    scenarioRepository.saveAll(scenarios);
                    sensorRepository.delete(sensor);
                    log.debug("Removed sensor {} from hub {}", sensorId, hubId);
                });
    }

    public List<ScenarioEntity> getScenariosForHub(String hubId) {
        return scenarioRepository.findByHubId(hubId);
    }

    private void createNewScenario(String hubId, ScenarioAddedEventAvro event) {
        ScenarioEntity scenario = new ScenarioEntity();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());
        for (ScenarioConditionAvro conditionAvro : event.getConditions()) {
            addConditionToScenario(scenario, hubId, conditionAvro);
        }
        for (DeviceActionAvro actionAvro : event.getActions()) {
            addActionToScenario(scenario, hubId, actionAvro);
        }
        scenarioRepository.save(scenario);
        log.info("Created new scenario: {} for hub: {}", scenario.getName(), hubId);
    }

    private void updateScenario(ScenarioEntity existing, String hubId, ScenarioAddedEventAvro event) {
        existing.setHubId(hubId);
        existing.getConditions().clear();
        existing.getActions().clear();
        for (ScenarioConditionAvro conditionAvro : event.getConditions()) {
            addConditionToScenario(existing, hubId, conditionAvro);
        }
        for (DeviceActionAvro actionAvro : event.getActions()) {
            addActionToScenario(existing, hubId, actionAvro);
        }
        scenarioRepository.save(existing);
        log.info("Updated scenario: {} for hub: {}", existing.getName(), hubId);
    }

    private void addConditionToScenario(ScenarioEntity scenario, String hubId,
                                        ScenarioConditionAvro conditionAvro) {
        String sensorId = conditionAvro.getSensorId();
        SensorEntity sensor = sensorRepository.findById(sensorId)
                .orElseGet(() -> {
                    SensorEntity newSensor = new SensorEntity(sensorId, hubId);
                    return sensorRepository.save(newSensor);
                });
        if (!sensor.getHubId().equals(hubId)) {
            log.warn("Sensor {} belongs to hub {}, but scenario is for hub {}. Updating...",
                    sensorId, sensor.getHubId(), hubId);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
        ConditionEntity condition = new ConditionEntity();
        condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));
        if (conditionAvro.getValue() instanceof Integer) {
            condition.setValue((Integer) conditionAvro.getValue());
        } else if (conditionAvro.getValue() instanceof Boolean) {
            condition.setValue((Boolean) conditionAvro.getValue() ? 1 : 0);
        }
        conditionRepository.save(condition);
        ScenarioConditionEntity scenarioCondition = new ScenarioConditionEntity();
        scenarioCondition.setScenario(scenario);
        scenarioCondition.setSensor(sensor);
        scenarioCondition.setCondition(condition);
        scenario.getConditions().add(scenarioCondition);
    }

    private void addActionToScenario(ScenarioEntity scenario, String hubId,
                                     DeviceActionAvro actionAvro) {
        String sensorId = actionAvro.getSensorId();
        SensorEntity sensor = sensorRepository.findById(sensorId)
                .orElseGet(() -> {
                    SensorEntity newSensor = new SensorEntity(sensorId, hubId);
                    return sensorRepository.save(newSensor);
                });
        if (!sensor.getHubId().equals(hubId)) {
            log.warn("Sensor {} belongs to hub {}, but scenario is for hub {}. Updating...",
                    sensorId, sensor.getHubId(), hubId);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
        ActionEntity action = new ActionEntity();
        action.setType(ActionType.valueOf(actionAvro.getType().name()));
        if (actionAvro.getValue() != null) {
            action.setValue(actionAvro.getValue());
        }
        actionRepository.save(action);
        ScenarioActionEntity scenarioAction = new ScenarioActionEntity();
        scenarioAction.setScenario(scenario);
        scenarioAction.setSensor(sensor);
        scenarioAction.setAction(action);
        scenario.getActions().add(scenarioAction);
    }
}