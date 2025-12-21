package az.et.central.service;

import az.et.central.model.dto.SensorDataDto;
import az.et.central.service.factory.SensorFactory;
import az.et.central.service.strategy.SensorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CentralSensorService {

    private final SensorFactory sensorFactory;

    public void processData(SensorDataDto data) {
        SensorStrategy strategy = sensorFactory.getStrategy(data.getSensorType());

        log.debug("Routing [ID: {}] to strategy: {}", data.getSensorId(), strategy.getClass().getSimpleName());
        strategy.process(data);
    }
}
