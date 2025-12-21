package az.et.central.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import az.et.central.messaging.event.BaseEvent;
import az.et.central.model.dto.SensorDataDto;
import az.et.central.service.CentralSensorService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@ExtendWith(MockitoExtension.class)
class CentralSensorEventConsumerTest {

    @Mock
    private Validator validator;

    @Mock
    private CentralSensorService sensorService;

    @InjectMocks
    private CentralSensorEventConsumer eventConsumer;

    @Test
    @DisplayName("Should validate and process event when data is correct")
    void shouldProcess_WhenEventIsValid() {
        SensorDataDto payload = SensorDataDto.builder()
                .sensorId("t1")
                .value(25.0)
                .build();
        BaseEvent<SensorDataDto> event = BaseEvent.<SensorDataDto>builder()
                .eventId(UUID.randomUUID())
                .payload(payload)
                .build();

        Message<BaseEvent<SensorDataDto>> message = MessageBuilder.withPayload(event).build();

        given(validator.validate(payload)).willReturn(Collections.emptySet());

        Consumer<Message<BaseEvent<SensorDataDto>>> consumer = eventConsumer.processSensorEvent();
        consumer.accept(message);

        then(sensorService).should(times(1)).processData(payload);
    }

    @Test
    @DisplayName("Should discard (ACK) when Event ID is missing")
    void shouldDiscard_WhenEventIdIsMissing() {
        BaseEvent<SensorDataDto> corruptEvent = BaseEvent.<SensorDataDto>builder()
                .eventId(null)
                .payload(null)
                .build();

        Message<BaseEvent<SensorDataDto>> message = MessageBuilder.withPayload(corruptEvent).build();

        eventConsumer.processSensorEvent().accept(message);

        then(sensorService).should(never()).processData(any());
        then(validator).should(never()).validate(any());
    }

    @Test
    @DisplayName("Should discard (ACK) when Payload is null")
    void shouldDiscard_WhenPayloadIsNull() {
        BaseEvent<SensorDataDto> event = BaseEvent.<SensorDataDto>builder()
                .eventId(UUID.randomUUID())
                .payload(null)
                .build();
        Message<BaseEvent<SensorDataDto>> message = MessageBuilder.withPayload(event).build();

        eventConsumer.processSensorEvent().accept(message);

        then(sensorService).should(never()).processData(any());
    }

    @Test
    @DisplayName("Should discard (ACK) when Validator finds violations")
    @SuppressWarnings("unchecked")
    void shouldDiscard_WhenValidationFails() {
        SensorDataDto invalidPayload = SensorDataDto.builder()
                .sensorId(null)
                .value(25.0)
                .build();

        BaseEvent<SensorDataDto> event = BaseEvent.<SensorDataDto>builder()
                .eventId(UUID.randomUUID())
                .payload(invalidPayload)
                .build();

        Message<BaseEvent<SensorDataDto>> message = MessageBuilder.withPayload(event).build();

        ConstraintViolation<SensorDataDto> violation = mock(ConstraintViolation.class);
        given(violation.getMessage()).willReturn("Sensor ID cannot be null");
        given(validator.validate(invalidPayload)).willReturn(Set.of(violation));

        eventConsumer.processSensorEvent().accept(message);

        then(sensorService).should(never()).processData(any());
    }
}