package az.et.central.service.strategy;

import az.et.central.model.dto.SensorDataDto;
import az.et.central.model.enums.SensorType;

public interface SensorStrategy {

    SensorType getSupportedType();

    void process(SensorDataDto data);
}
