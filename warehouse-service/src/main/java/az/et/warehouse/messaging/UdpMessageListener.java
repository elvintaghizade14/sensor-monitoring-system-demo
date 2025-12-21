package az.et.warehouse.messaging;

import static az.et.warehouse.model.constants.ApplicationConstant.CHANNEL_DEBUG;
import static az.et.warehouse.model.constants.ApplicationConstant.CHANNEL_HUMIDITY;
import static az.et.warehouse.model.constants.ApplicationConstant.CHANNEL_TEMPERATURE;

import az.et.warehouse.model.enums.SensorType;
import az.et.warehouse.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UdpMessageListener {

    private final SensorService sensorService;

    @ServiceActivator(inputChannel = CHANNEL_TEMPERATURE)
    public void handleTemperature(byte[] payload) {
        process(payload, SensorType.TEMPERATURE);
    }

    @ServiceActivator(inputChannel = CHANNEL_HUMIDITY)
    public void handleHumidity(byte[] payload) {
        process(payload, SensorType.HUMIDITY);
    }

    @ServiceActivator(inputChannel = CHANNEL_DEBUG)
    public void handleDebug(byte[] payload) {
        process(payload, SensorType.UNKNOWN);
    }

    private void process(byte[] payload, SensorType type) {
        String message = new String(payload).trim();

        log.debug("Received [{}]: {}", type, message);

        sensorService.processRawSensorData(message, type);
    }
}
