package az.et.central.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "central.threshold")
public class ThresholdProperties {

    @NotNull(message = "Temperature threshold must be defined in application.yml")
    private Double temperature;

    @NotNull(message = "Humidity threshold must be defined in application.yml")
    private Double humidity;
}
