package az.et.central.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationConstant {

    public static class RabbitConstant {
        public static final String BINDING_SENSOR_OUT = "sensor-out-0";
        public static final String HEADER_SENSOR_ID = "sensor-id";
    }
}
