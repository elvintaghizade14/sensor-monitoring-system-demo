package az.et.warehouse.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationConstant {

    public static final String CHANNEL_TEMPERATURE = "temperatureChannel";
    public static final String CHANNEL_HUMIDITY = "humidityChannel";
    public static final String CHANNEL_DEBUG = "debugChannel";

    public static class RabbitConstant {
        public static final String BINDING_SENSOR_OUT = "sensor-out-0";
        public static final String HEADER_SENSOR_ID = "sensor-id";
    }
}
