package az.et.central.service.strategy;

import az.et.central.config.ThresholdProperties;
import az.et.central.model.dto.SensorDataDto;
import az.et.central.model.enums.SensorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemperatureStrategy implements SensorStrategy {

    private final ThresholdProperties props;

    @Override
    public SensorType getSupportedType() {
        return SensorType.TEMPERATURE;
    }

    @Override
    public void process(SensorDataDto data) {
        Double sensorValue = data.getValue();
        Double threshold = props.getTemperature();

        if (sensorValue > threshold) {
            log.error("ALARM: Temperature High! [ID: {}] [Value: {}°C] [Threshold: {}°C]",
                    data.getSensorId(), sensorValue, threshold);
            // TODO: Trigger Siren / Email
        } else {
            log.info("Temperature Normal: [ID: {}] [Value: {}°C] [Threshold: {}°C]",
                    data.getSensorId(), sensorValue, threshold);
        }
    }
}
