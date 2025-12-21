package az.et.warehouse.messaging.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent<T> {

    private UUID eventId;
    private LocalDateTime eventTime;
    private T payload;

    public static <T> BaseEvent<T> of(T payload) {
        return BaseEvent.<T>builder()
                .eventId(UUID.randomUUID())
                .eventTime(LocalDateTime.now())
                .payload(payload)
                .build();
    }
}