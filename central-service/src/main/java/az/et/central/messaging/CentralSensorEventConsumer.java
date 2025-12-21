package az.et.central.messaging;

import az.et.central.messaging.event.BaseEvent;
import az.et.central.model.dto.SensorDataDto;
import az.et.central.service.CentralSensorService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CentralSensorEventConsumer {

    private final Validator validator;
    private final CentralSensorService sensorService;

    @Bean
    public Consumer<Message<BaseEvent<SensorDataDto>>> processSensorEvent() {
        return message -> {
            final BaseEvent<SensorDataDto> event = message.getPayload();
            if (!validateEvent(event)) {
                return; // ACK
            }

            final String eventId = event.getEventId().toString();
            try (MDC.MDCCloseable ignored = MDC.putCloseable("eventId", eventId)) {
                log.info("Consuming event [ID: {}]", eventId);

                sensorService.processData(event.getPayload());
            } catch (Exception e) {
                log.error("Failure! Event [ID: {}] Payload: [{}] Error: ", event.getEventId(), event.getPayload(), e);
                throw e;
            }
        };
    }

    private boolean validateEvent(BaseEvent<SensorDataDto> event) {
        if (event == null || event.getEventId() == null) {
            log.error("Discarding corrupt event: Missing Event Object or ID.");
            return false;
        }

        if (event.getPayload() == null) {
            log.warn("Discarding Event [ID: {}]: Payload is NULL", event.getEventId());
            return false;
        }

        Set<ConstraintViolation<SensorDataDto>> violations = validator.validate(event.getPayload());
        if (!violations.isEmpty()) {
            log.warn("Discarding Invalid Data [ID: {}]:", event.getEventId());
            violations.forEach(v -> log.warn("   - {}: {}", v.getPropertyPath(), v.getMessage()));
            return false;
        }
        return true;
    }
}
