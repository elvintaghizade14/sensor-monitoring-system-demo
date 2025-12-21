package az.et.central.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import az.et.central.config.ThresholdProperties;
import az.et.central.model.dto.SensorDataDto;
import az.et.central.model.enums.SensorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HumidityStrategyTest {

    @Mock
    private ThresholdProperties properties;

    @InjectMocks
    private HumidityStrategy strategy;

    @Test
    @DisplayName("Should return HUMIDITY as supported type")
    void shouldReturnSupportedType() {
        assertThat(strategy.getSupportedType()).isEqualTo(SensorType.HUMIDITY);
    }

    @Test
    @DisplayName("Should process without error when humidity is high")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldProcess_WhenHumidityIsHigh() {
        given(properties.getHumidity()).willReturn(50.0);
        SensorDataDto data = SensorDataDto.builder().sensorId("h1").value(80.0).build();

        strategy.process(data);

        then(properties).should(times(1)).getHumidity();
    }
}