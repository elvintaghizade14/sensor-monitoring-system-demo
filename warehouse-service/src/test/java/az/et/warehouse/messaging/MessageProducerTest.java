package az.et.warehouse.messaging;

import static az.et.warehouse.model.constants.ApplicationConstant.RabbitConstant.BINDING_SENSOR_OUT;
import static az.et.warehouse.model.constants.ApplicationConstant.RabbitConstant.HEADER_SENSOR_ID;
import static az.et.warehouse.model.enums.SensorType.TEMPERATURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import az.et.warehouse.messaging.event.BaseEvent;
import az.et.warehouse.model.dto.SensorDataDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private MessageProducer messageProducer;

    @Captor
    private ArgumentCaptor<Message<BaseEvent<SensorDataDto>>> messageCaptor;

    @Test
    @DisplayName("Should build message with headers and send to StreamBridge")
    void shouldSend_WhenStreamBridgeSucceeds() {
        SensorDataDto dto = SensorDataDto.builder().sensorId("t1").sensorType(TEMPERATURE).build();
        BaseEvent<SensorDataDto> event = BaseEvent.of(dto);
        given(streamBridge.send(eq(BINDING_SENSOR_OUT), any(Message.class)))
                .willReturn(true);

        messageProducer.sendSensorData(event);

        then(streamBridge).should(times(1)).send(eq(BINDING_SENSOR_OUT), messageCaptor.capture());

        Message<BaseEvent<SensorDataDto>> capturedMsg = messageCaptor.getValue();
        assertThat(capturedMsg.getPayload()).isEqualTo(event);
        assertThat(capturedMsg.getHeaders()).containsKey(HEADER_SENSOR_ID);
        assertThat(capturedMsg.getHeaders().get(HEADER_SENSOR_ID)).isEqualTo("t1");
    }

    @Test
    @DisplayName("Should handle exception gracefully when Broker is down")
    void shouldNotThrow_WhenBrokerFails() {
        SensorDataDto dto = SensorDataDto.builder().sensorId("t1").value(25.5).sensorType(TEMPERATURE).build();
        BaseEvent<SensorDataDto> event = BaseEvent.of(dto);

        given(streamBridge.send(any(), any())).willThrow(new RuntimeException("Broker Down"));

        assertThatCode(() -> messageProducer.sendSensorData(event)).doesNotThrowAnyException();

        then(streamBridge).should(times(1)).send(eq(BINDING_SENSOR_OUT), any());
    }
}