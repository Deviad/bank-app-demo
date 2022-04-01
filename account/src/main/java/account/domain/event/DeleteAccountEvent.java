package account.domain.event;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public class DeleteAccountEvent implements Event {
    public static final String TYPE = "delete.account.event";
    private final String eventId;
    private final Map<String, Object> payload;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Status status;
    private String exception;
    private String exceptionType;
    private final EventType eventType;
    private final boolean retryable;
    private final int retryAttempts;


    public DeleteAccountEvent(String eventId,
                              Map<String, Object> payload,
                              Instant createdAt,
                              Instant updatedAt,
                              Status status,
                              String exception,
                              String exceptionType) {

        this.eventId = eventId;
        this.payload = payload;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.exception = exception;
        this.exceptionType = exceptionType;
        this.eventType = EventType.ACCOUNT_CREATE;
        this.retryable = true;
        this.retryAttempts = 0;
    }

    public DeleteAccountEvent(String eventId,
                              Map<String, Object> payload,
                              Instant createdAt,
                              Instant updatedAt,
                              Status status,
                              boolean retryable,
                              int retryAttempts,
                              String exception,
                              String exceptionType) {

        this.eventId = eventId;
        this.payload = payload;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.eventType = EventType.ACCOUNT_CREATE;
        this.retryable = retryable;
        this.retryAttempts = retryAttempts;
        this.exception = exception;
        this.exceptionType = exceptionType;
    }
}
