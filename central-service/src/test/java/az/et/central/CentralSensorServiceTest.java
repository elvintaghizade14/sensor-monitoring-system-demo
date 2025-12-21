package az.et.central;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import az.et.central.model.dto.SensorDataDto;
import az.et.central.model.enums.SensorType;
import az.et.central.service.CentralSensorService;
import az.et.central.service.factory.SensorFactory;
import az.et.central.service.strategy.SensorStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CentralSensorServiceTest {

    @Mock
    private SensorFactory sensorFactory;

    @InjectMocks
    private CentralSensorService centralSensorService;

    @Test
    @DisplayName("Should route data to the correct strategy retrieved from factory")
    void shouldRouteToStrategy() {
        SensorType type = SensorType.TEMPERATURE;
        SensorDataDto data = SensorDataDto.builder()
                .sensorId("t1")
                .sensorType(type)
                .value(25.0)
                .build();

        SensorStrategy mockStrategy = mock(SensorStrategy.class);
        given(sensorFactory.getStrategy(type)).willReturn(mockStrategy);

        centralSensorService.processData(data);

        then(sensorFactory).should(times(1)).getStrategy(type);
        then(mockStrategy).should(times(1)).process(data);
    }

    @Test
    @DisplayName("Should propagate exception if Factory fails (e.g., Unknown Type)")
    void shouldPropagateFactoryError() {
        SensorType type = SensorType.UNKNOWN;
        SensorDataDto data = SensorDataDto.builder()
                .sensorType(type)
                .build();

        given(sensorFactory.getStrategy(type))
                .willThrow(new IllegalArgumentException("Unsupported Type"));

        assertThatThrownBy(() -> centralSensorService.processData(data))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported Type");
    }
}