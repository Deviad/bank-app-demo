package account.domain.event;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import account.infrastructure.ApiException;
import account.infrastructure.MappingUtils;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@Context
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EventHandler {
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher<Event> eventPublisher;


    @EventListener
    void save(Event event) {

        try {
            var ee = new EventEntity()
                    .withEventId(event.eventId())
                    .withPayload(event.payload())
                    .withStatus(event.status())
                    .withEventType(event.eventType())
                    .withCreatedAt(event.createdAt())
                    .withUpdatedAt(event.updatedAt())
                    .withRetryable(event.retryable())
                    .withRetryAttempts(event.retryAttempts())
                    .withException(event.exception())
                    .withExceptionType(event.exceptionType());
            saveEvent(ee);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void saveEvent(EventEntity event) {

        if (eventRepository.findById(event.getEventId()).isEmpty()) {
            eventRepository.save(event);
        } else {
            eventRepository.update(event);
        }
    }

    @Scheduled(fixedRate = "10m")
    void retryFailedEvents() {

        List<EventEntity> failedEvents = eventRepository
                .getRetryableFailedEvents();
        for (var event : failedEvents) {
            var newE = event
                    .withStatus(Event.Status.RETRY)
                    .withRetryAttempts(1);
            eventRepository.save(newE);
            eventPublisher.publishEvent(MappingUtils.MAPPER.convertValue(event, Event.class));
        }
    }

}
