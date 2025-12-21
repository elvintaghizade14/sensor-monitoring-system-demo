package az.et.warehouse.service;

import az.et.warehouse.mapper.SensorDataMapper;
import az.et.warehouse.messaging.MessageProducer;
import az.et.warehouse.messaging.event.BaseEvent;
import az.et.warehouse.model.dto.SensorDataDto;
import az.et.warehouse.model.enums.SensorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorDataMapper sensorDataMapper;
    private final MessageProducer messageProducer;

    public void processRawSensorData(String rawMessage, SensorType type) {
        sensorDataMapper.toDto(rawMessage, type)
                .ifPresentOrElse(
                        this::processSensorData,
                        () -> log.warn("Dropped invalid message: [{}] Type: [{}]", rawMessage, type)
                );
    }

    protected void processSensorData(SensorDataDto data) {
        final BaseEvent<SensorDataDto> event = BaseEvent.of(data);

        log.debug("Processing Event [ID: {}]: [Type: {}] [SensorID: {}] [Val: {}]",
                event.getEventId(),
                data.getSensorType(),
                data.getSensorId(),
                data.getValue());

        messageProducer.sendSensorData(event);
        log.debug("Completed processing Event [ID: {}]", event.getEventId());
    }
}
