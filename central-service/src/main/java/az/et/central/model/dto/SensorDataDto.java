package az.et.central.model.dto;

import az.et.central.model.enums.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataDto {

    @NotBlank(message = "Sensor ID cannot be empty")
    private String sensorId;

    @NotNull(message = "Value cannot be null")
    private Double value;

    @NotNull(message = "Sensor Type is required")
    private SensorType sensorType;
}