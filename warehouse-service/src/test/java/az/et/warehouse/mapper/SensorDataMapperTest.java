package az.et.warehouse.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import az.et.warehouse.model.dto.SensorDataDto;
import az.et.warehouse.model.enums.SensorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SensorDataMapperTest {

    private final SensorDataMapper mapper = new SensorDataMapper();

    @Test
    @DisplayName("Should correctly map valid string to DTO")
    void shouldMapValidString() {
        String raw = "sensor_id=s1;value=55.5";

        Optional<SensorDataDto> result = mapper.toDto(raw, SensorType.TEMPERATURE);

        assertThat(result).isPresent();
        assertThat(result.get())
                .extracting("sensorId", "value", "sensorType")
                .containsExactly("s1", 55.5, SensorType.TEMPERATURE);
    }

    @ParameterizedTest
    @DisplayName("Should return empty for invalid inputs")
    @NullAndEmptySource // Tests null and ""
    @ValueSource(strings = {
            "sensor_ids1value55.5",      // Missing semicolon
            "sensor_id=s1",              // Missing value
            "value=55.5",                // Missing ID
            "sensor_id=s1;value=abc",    // Non-numeric value
            "sensor_id=;value=55.5",     // Empty ID
            "garbage_data_string"        // Complete garbage
    })
    void shouldReturnEmptyForInvalidInputs(String invalidInput) {
        Optional<SensorDataDto> result = mapper.toDto(invalidInput, SensorType.HUMIDITY);

        assertThat(result).isEmpty();
    }
}
