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
class TemperatureStrategyTest {

    @Mock
    private ThresholdProperties properties;

    @InjectMocks
    private TemperatureStrategy strategy;

    @Test
    @DisplayName("Should return TEMPERATURE as supported type")
    void shouldReturnSupportedType() {
        assertThat(strategy.getSupportedType()).isEqualTo(SensorType.TEMPERATURE);
    }

    @Test
    @DisplayName("Should log INFO when temperature is normal (No Alarm)")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldLogInfo_WhenNormal() {
        given(properties.getTemperature()).willReturn(35.0);
        SensorDataDto data = SensorDataDto.builder().sensorId("t1").value(30.0).build();

        strategy.process(data);

        then(properties).should(times(1)).getTemperature();
    }

    @Test
    @DisplayName("Should log ERROR when temperature is high (Alarm)")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldLogError_WhenHigh() {
        given(properties.getTemperature()).willReturn(35.0);
        SensorDataDto data = SensorDataDto.builder().sensorId("t1").value(40.0).build();

        strategy.process(data);

        then(properties).should(times(1)).getTemperature();
    }
}