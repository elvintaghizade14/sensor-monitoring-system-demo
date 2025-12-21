package az.et.warehouse.model.dto;

import az.et.warehouse.model.enums.SensorType;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDataDto implements Serializable {

    private String sensorId;
    private Double value;
    private SensorType sensorType;
}
