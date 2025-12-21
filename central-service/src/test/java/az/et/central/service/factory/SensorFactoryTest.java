package az.et.central.service.factory;

import static az.et.central.model.enums.SensorType.HUMIDITY;
import static az.et.central.model.enums.SensorType.TEMPERATURE;
import static az.et.central.model.enums.SensorType.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import az.et.central.service.strategy.DieStrategy;
import az.et.central.service.strategy.SensorStrategy;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SensorFactoryTest {

    @Mock
    private SensorStrategy temperatureStrategy;

    @Mock
    private SensorStrategy humidityStrategy;

    @Mock
    private DieStrategy dieStrategy;

    private SensorFactory sensorFactory;

    @BeforeEach
    void setUp() {
        given(temperatureStrategy.getSupportedType()).willReturn(TEMPERATURE);
        given(humidityStrategy.getSupportedType()).willReturn(HUMIDITY);
        given(dieStrategy.getSupportedType()).willReturn(UNKNOWN);

        sensorFactory = new SensorFactory(List.of(temperatureStrategy, humidityStrategy, dieStrategy));

        then(temperatureStrategy).should(times(1)).getSupportedType();
        then(humidityStrategy).should(times(1)).getSupportedType();
        then(dieStrategy).should(times(1)).getSupportedType();
    }

    @Test
    @DisplayName("Should return correct strategy for configured type")
    void shouldReturnStrategy_WhenTypeIsSupported() {
        SensorStrategy result = sensorFactory.getStrategy(TEMPERATURE);
        assertThat(result).isSameAs(temperatureStrategy);
    }
}