package account.domain.event;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Map;

import account.infrastructure.PayloadConverter;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Introspected
@With
public class EventEntity implements Event {

    @Id
    private String eventId;

    @Convert(converter = PayloadConverter.class)
    @Column(name = "payload", columnDefinition="text")
    private Map<String, Object> payload;
    private Instant createdAt;
    private Instant updatedAt;
    @Enumerated(EnumType.STRING)
    private Event.Status status;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private boolean isRetryable;
    private int retryAttempts;
    @Column(name = "exception", columnDefinition="text")
    private String exception;
    private String exceptionType;


    @Override
    public String eventId() {

        return eventId;
    }

    @Override
    public Map<String, Object> payload() {

        return payload;
    }

    @Override
    public Instant createdAt() {

        return createdAt;
    }

    @Override
    public Instant updatedAt() {

        return updatedAt;
    }

    @Override
    public Status status() {

        return status;
    }

    @Override
    public EventType eventType() {

        return eventType;
    }

    @Override
    public boolean retryable() {

        return isRetryable;
    }

    @Override
    public int retryAttempts() {

        return retryAttempts;
    }

    @Override
    public String exception() {

        return exception;
    }

    @Override
    public String exceptionType() {

        return exception;
    }

    public boolean getIsRetryable() {
        return isRetryable;
    }
}
