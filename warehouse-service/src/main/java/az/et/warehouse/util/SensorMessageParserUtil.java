package az.et.warehouse.util;

import az.et.warehouse.model.dto.ParseResult;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SensorMessageParserUtil {

    /**
     * Pre-compile the pattern for performance (Thread-safe)
     * Pattern explains:
     * 1. "sensor_id\s*=\s*"  -> matches literal 'sensor_id=' with optional spaces
     * 2. "(?<id>[^;]+)"      -> captures everything until the next ';' into group 'id'
     * 3. ";\s*"              -> matches the separator ';' and optional space
     * 4. "value\s*=\s*"      -> matches literal 'value=' with optional spaces
     * 5. "(?<value>.+)"      -> captures the rest of the string into group 'value'
     */
    private static final Pattern SENSOR_PATTERN =
            Pattern.compile("sensor_id\\s*=\\s*(?<id>[^;]+);\\s*value\\s*=\\s*(?<value>.+)");

    public static Optional<ParseResult> parse(String payload) {
        if (payload == null || payload.isBlank()) {
            return Optional.empty();
        }

        Matcher matcher = SENSOR_PATTERN.matcher(payload.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        return Optional.of(new ParseResult(
                matcher.group("id").trim(),
                matcher.group("value").trim()
        ));
    }
}
