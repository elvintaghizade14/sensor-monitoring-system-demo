package az.et.warehouse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import az.et.warehouse.mapper.SensorDataMapper;
import az.et.warehouse.messaging.MessageProducer;
import az.et.warehouse.messaging.event.BaseEvent;
import az.et.warehouse.model.dto.SensorDataDto;
import az.et.warehouse.model.enums.SensorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorDataMapper sensorDataMapper;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private SensorService sensorService;

    @Captor
    private ArgumentCaptor<BaseEvent<SensorDataDto>> eventCaptor;

    @Test
    @DisplayName("Should parse valid data and publish event")
    void shouldProcessAndPublish_WhenDataIsValid() {
        String rawMessage = "sensor_id=t1;value=42.5";
        SensorType type = SensorType.TEMPERATURE;
        SensorDataDto expectedDto = SensorDataDto.builder()
                .sensorId("t1")
                .value(42.5)
                .sensorType(type)
                .build();
        given(sensorDataMapper.toDto(rawMessage, type))
                .willReturn(Optional.of(expectedDto));

        sensorService.processRawSensorData(rawMessage, type);

        then(sensorDataMapper).should(times(1)).toDto(rawMessage, type);
        then(messageProducer).should(times(1)).sendSensorData(eventCaptor.capture());

        BaseEvent<SensorDataDto> capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getPayload().getSensorId()).isEqualTo("t1");
        assertThat(capturedEvent.getPayload().getValue()).isEqualTo(42.5);
        assertThat(capturedEvent.getPayload().getSensorType()).isEqualTo(SensorType.TEMPERATURE);
    }

    @Test
    @DisplayName("Should drop message when Mapper returns empty (Invalid Data)")
    void shouldDropMessage_WhenDataIsInvalid() {
        String rawMessage = "invalid_data";
        SensorType type = SensorType.HUMIDITY;

        given(sensorDataMapper.toDto(rawMessage, type))
                .willReturn(Optional.empty());

        sensorService.processRawSensorData(rawMessage, type);

        then(sensorDataMapper).should(times(1)).toDto(rawMessage, type);
        then(messageProducer).should(never()).sendSensorData(eventCaptor.capture());
    }
}
