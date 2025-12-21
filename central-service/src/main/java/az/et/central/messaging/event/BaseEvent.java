package az.et.central.messaging.event;

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
}