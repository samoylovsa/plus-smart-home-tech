package ru.yandex.practicum.smarthome.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ActionEntity;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ConditionEntity;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ConditionType;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ScenarioEntity;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioActionService {

    private final ScenarioService scenarioService;
    private final GrpcClientService grpcClientService;

    @Transactional
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        List<ScenarioEntity> scenarios = scenarioService.getScenariosForHub(snapshot.getHubId());
        scenarios.forEach(scenario -> {
            if (isScenarioTriggered(scenario, snapshot)) {
                executeScenarioActions(scenario, snapshot);
            }
        });
    }

    private boolean isScenarioTriggered(ScenarioEntity scenario, SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        return scenario.getConditions().stream()
                .allMatch(scenarioCondition -> {
                    String sensorId = scenarioCondition.getSensor().getId();
                    SensorStateAvro sensorState = sensorsState.get(sensorId);
                    if (sensorState == null) {
                        return false;
                    }
                    return checkCondition(scenarioCondition.getCondition(), sensorState);
                });
    }

    private boolean checkCondition(ConditionEntity condition, SensorStateAvro sensorState) {
        Object sensorData = sensorState.getData();
        int sensorValue = extractValueFromData(sensorData, condition.getType());
        int conditionValue = condition.getValue() != null ? condition.getValue() : 0;
        return switch (condition.getOperation()) {
            case EQUALS -> sensorValue == conditionValue;
            case GREATER_THAN -> sensorValue > conditionValue;
            case LOWER_THAN -> sensorValue < conditionValue;
        };
    }

    private int extractValueFromData(Object data, ConditionType type) {
        if (data instanceof ClimateSensorAvro climateData) {
            return switch (type) {
                case TEMPERATURE -> climateData.getTemperatureC();
                case HUMIDITY -> climateData.getHumidity();
                case CO2LEVEL -> climateData.getCo2Level();
                default -> throw new IllegalArgumentException("Condition type " + type + " not supported for ClimateSensor");
            };
        } else if (data instanceof TemperatureSensorAvro tempData) {
            if (type == ConditionType.TEMPERATURE) {
                return tempData.getTemperatureC();
            }
            throw new IllegalArgumentException("TemperatureSensor only supports TEMPERATURE condition, got: " + type);
        } else if (data instanceof LightSensorAvro lightData) {
            if (type == ConditionType.LUMINOSITY) {
                return lightData.getLuminosity();
            }
            throw new IllegalArgumentException("LightSensor only supports LUMINOSITY condition, got: " + type);
        } else if (data instanceof MotionSensorAvro motionData) {
            if (type == ConditionType.MOTION) {
                return motionData.getMotion() ? 1 : 0;
            }
            throw new IllegalArgumentException("MotionSensor only supports MOTION condition, got: " + type);
        } else if (data instanceof SwitchSensorAvro switchData) {
            if (type == ConditionType.SWITCH) {
                return switchData.getState() ? 1 : 0;
            }
            throw new IllegalArgumentException(
                    "SwitchSensor only supports SWITCH condition, got: " + type);
        }
        throw new IllegalArgumentException("Unknown sensor data type: " + data.getClass());
    }

    private void executeScenarioActions(ScenarioEntity scenario, SensorsSnapshotAvro snapshot) {
        scenario.getActions().forEach(scenarioAction -> {
            String sensorId = scenarioAction.getSensor().getId();
            ActionEntity action = scenarioAction.getAction();
            grpcClientService.sendDeviceAction(
                    scenario.getHubId(),
                    scenario.getName(),
                    sensorId,
                    action.getType(),
                    action.getValue(),
                    snapshot.getTimestamp().toEpochMilli()
            );
            log.info("Executed action for scenario: {}, sensor: {}, action: {}",
                    scenario.getName(), sensorId, action.getType());
        });
    }
}
