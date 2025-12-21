package az.et.central.service.factory;

import az.et.central.model.enums.SensorType;
import az.et.central.service.strategy.SensorStrategy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class SensorFactory {

    private final Map<SensorType, SensorStrategy> strategyMap;

    public SensorFactory(List<SensorStrategy> strategies) {
        this.strategyMap = strategies
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        SensorStrategy::getSupportedType,
                        Function.identity()));
    }

    public SensorStrategy getStrategy(SensorType type) {
        return Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported Sensor Type: " + type));
    }
}
