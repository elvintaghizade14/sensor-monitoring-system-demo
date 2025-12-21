package az.et.warehouse.config.properties;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "warehouse.udp")
public class WarehouseProperties {

    @Min(value = 1024, message = "Temperature Port must be a non-privileged port (>1024)")
    private int temperaturePort;

    @Min(value = 1024, message = "Humidity Port must be a non-privileged port (>1024)")
    private int humidityPort;

    @Min(value = 1024, message = "Debug Port must be a non-privileged port (>1024)")
    private int debugPort;
}
