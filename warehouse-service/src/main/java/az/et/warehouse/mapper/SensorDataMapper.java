package az.et.warehouse.mapper;

import az.et.warehouse.model.dto.ParseResult;
import az.et.warehouse.model.dto.SensorDataDto;
import az.et.warehouse.model.enums.SensorType;
import az.et.warehouse.util.SensorMessageParserUtil;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SensorDataMapper {

    public Optional<SensorDataDto> toDto(String rawMessage, SensorType type) {
        return SensorMessageParserUtil.parse(rawMessage)
                .flatMap(result -> buildDto(result, type));
    }

    private Optional<SensorDataDto> buildDto(ParseResult result, SensorType type) {
        try {
            return Optional.of(SensorDataDto.builder()
                    .sensorId(result.id())
                    .value(Double.valueOf(result.value()))
                    .sensorType(type)
                    .build());
        } catch (NumberFormatException e) {
            log.warn("Invalid numeric value for [{}]: {}", result.id(), result.value());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error mapping UDP message", e);
            return Optional.empty();
        }
    }
}