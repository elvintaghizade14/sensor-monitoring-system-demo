package az.et.central.service.strategy;

import az.et.central.model.dto.SensorDataDto;
import az.et.central.model.enums.SensorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DieStrategy implements SensorStrategy {

    @Override
    public SensorType getSupportedType() {
        return SensorType.UNKNOWN;
    }

    @Override
    public void process(SensorDataDto data) {
        log.info("🧪 DEBUG STRATEGY: Processing event... [ID: {}]", data.getSensorId());

        if ("die".equals(data.getSensorId())) {
            log.warn("FAILURE TRIGGERED: Simulating runtime crash for [ID: {}]. RabbitMQ should catch this and RETRY.",
                    data.getSensorId());

            throw new RuntimeException("Simulated Crash for Retry & DLQ verification!");
        }

        log.info("DEBUG STRATEGY: Processed successfully (No Crash).");
    }
}
