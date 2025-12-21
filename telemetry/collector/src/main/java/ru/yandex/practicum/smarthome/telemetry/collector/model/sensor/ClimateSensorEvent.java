package ru.yandex.practicum.smarthome.telemetry.collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {

    @NotNull(message = "Температура не может быть null")
    private Integer temperatureC;

    @NotNull(message = "Влажность не может быть null")
    private Integer humidity;

    @NotNull(message = "Уровень CO2 не может быть null")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}