package az.et.warehouse.messaging;

import static az.et.warehouse.model.enums.SensorType.HUMIDITY;
import static az.et.warehouse.model.enums.SensorType.TEMPERATURE;
import static az.et.warehouse.model.enums.SensorType.UNKNOWN;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import az.et.warehouse.service.SensorService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UdpMessageListenerTest {

    @Mock
    private SensorService sensorService;

    @InjectMocks
    private UdpMessageListener udpMessageListener;

    @Test
    @DisplayName("Should route Temperature channel data with correct Enum")
    void shouldHandleTemperature() {
        String rawString = "sensor_id=t1;value=20.0";
        byte[] payload = rawString.getBytes(StandardCharsets.UTF_8);

        udpMessageListener.handleTemperature(payload);

        then(sensorService).should(times(1)).processRawSensorData(rawString, TEMPERATURE);
    }

    @Test
    @DisplayName("Should route Humidity channel data with correct Enum")
    void shouldHandleHumidity() {
        String rawString = "sensor_id=h1;value=80.0";
        byte[] payload = rawString.getBytes(StandardCharsets.UTF_8);

        udpMessageListener.handleHumidity(payload);

        then(sensorService).should(times(1)).processRawSensorData(rawString, HUMIDITY);
    }

    @Test
    @DisplayName("Should route Debug channel data as UNKNOWN type")
    void shouldHandleDebug() {
        String rawString = "sensor_id=d1;value=0";
        byte[] payload = rawString.getBytes(StandardCharsets.UTF_8);

        udpMessageListener.handleDebug(payload);

        then(sensorService).should(times(1)).processRawSensorData(rawString, UNKNOWN);
    }

    @Test
    @DisplayName("Should trim whitespace from payload before processing")
    void shouldTrimPayload() {
        String rawString = "   sensor_id=t1;value=50.0   \n";
        String expectedString = "sensor_id=t1;value=50.0";
        byte[] payload = rawString.getBytes(StandardCharsets.UTF_8);

        udpMessageListener.handleTemperature(payload);

        then(sensorService).should(times(1)).processRawSensorData(expectedString, TEMPERATURE);
    }
}