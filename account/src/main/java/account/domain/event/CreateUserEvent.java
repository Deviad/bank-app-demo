package account.domain.event;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@Getter
public class CreateUserEvent implements Event {
    public static final String TYPE = "create.user.event";

    String eventId;
    Map<String, Object> payload;
    Instant createdAt;
    Instant updatedAt;
    Event.Status status;
    EventType eventType = EventType.USER_CREATE;
    boolean retryable = true;
    int retryAttempts = 0;
    String exception;
    String exceptionType;
}
