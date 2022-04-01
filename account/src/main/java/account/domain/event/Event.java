package account.domain.event;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = CreateAccountEvent.TYPE, value = CreateAccountEvent.class),
        @JsonSubTypes.Type(name = CreateUserEvent.TYPE, value = CreateUserEvent.class),
        @JsonSubTypes.Type(name = DeleteAccountEvent.TYPE, value = DeleteAccountEvent.class),
        @JsonSubTypes.Type(name = DeleteUserEvent.TYPE, value = DeleteUserEvent.class)
})
public interface Event {
    String eventId();
    Map<String, Object> payload();
    Instant createdAt();
    Instant updatedAt();
    Status status();
    EventType eventType();
    boolean retryable();
    int retryAttempts();
    String exception();
    String exceptionType();

    @ToString
    @AllArgsConstructor
    @Getter
    enum Status {
        RECEIVED("RECEIVED"),
        ENTITY_CREATED("ENTITY_CREATED"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED"),
        RETRY("RETRY");

        private String value;
    }
}
