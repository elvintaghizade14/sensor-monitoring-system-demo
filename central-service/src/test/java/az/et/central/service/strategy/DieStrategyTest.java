package az.et.central.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import az.et.central.model.dto.SensorDataDto;
import az.et.central.model.enums.SensorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DieStrategyTest {

    @InjectMocks
    private DieStrategy strategy;

    @Test
    @DisplayName("Should return UNKNOWN as supported type")
    void shouldReturnSupportedType() {
        assertThat(strategy.getSupportedType()).isEqualTo(SensorType.UNKNOWN);
    }

    @Test
    @DisplayName("Should throw RuntimeException when ID is 'die' (Simulates Crash)")
    void shouldCrash_WhenIdIsDie() {
        SensorDataDto data = SensorDataDto.builder().sensorId("die").value(0.0).build();

        assertThatThrownBy(() -> strategy.process(data))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated Crash");
    }

    @Test
    @DisplayName("Should process normally when ID is NOT 'die'")
    void shouldNotCrash_WhenIdIsNormal() {
        SensorDataDto data = SensorDataDto.builder().sensorId("other").value(0.0).build();

        assertThatCode(() -> strategy.process(data))
                .doesNotThrowAnyException();
    }
}