package az.et.warehouse.messaging;

import static az.et.warehouse.model.constants.ApplicationConstant.RabbitConstant.BINDING_SENSOR_OUT;
import static az.et.warehouse.model.constants.ApplicationConstant.RabbitConstant.HEADER_SENSOR_ID;

import az.et.warehouse.messaging.event.BaseEvent;
import az.et.warehouse.model.dto.SensorDataDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final StreamBridge streamBridge;

    public void sendSensorData(BaseEvent<SensorDataDto> event) {
        final Message<BaseEvent<SensorDataDto>> message = MessageBuilder
                .withPayload(event)
                .setHeader(HEADER_SENSOR_ID, event.getPayload().getSensorId())
                .build();

        final UUID eventId = event.getEventId();
        log.info("Sending Event [ID: {}]", eventId);

        try {
            boolean isSent = streamBridge.send(BINDING_SENSOR_OUT, message);
            if (!isSent) {
                log.error("Broker rejected message! Event [ID: {}]", eventId);
            }
        } catch (Exception e) {
            log.error("FATAL: Failed to send Event [ID: {}]. Error: ", eventId, e);
        }
    }
}