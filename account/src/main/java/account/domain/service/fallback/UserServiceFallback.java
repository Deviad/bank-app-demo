
package account.domain.service.fallback;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

import account.application.service.UserFacade;
import account.domain.DomainException;
import account.domain.event.CreateUserEvent;
import account.domain.event.DeleteUserEvent;
import account.domain.event.Event;
import account.domain.event.EventRepository;
import account.domain.model.AccountType;
import account.domain.model.User;
import account.infrastructure.MappingUtils;
import static account.infrastructure.Utils.throwIfExcluded;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.retry.annotation.Fallback;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Fallback(excludes = {ConstraintViolationException.class, NoSuchElementException.class})
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class UserServiceFallback {
    public static final String USER_ID = "userId";
    private final ApplicationEventPublisher<Event> eventPublisher;
    private EventRepository eventRepository;

    //This is because when there's an error the connection is closed and a new one must be created
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void createUser(UserFacade.CreateUserParameterObject object) {

        throwIfExcluded(eventRepository, object.getEventId());

        eventPublisher.publishEvent(new CreateUserEvent(
                object.getEventId(),
                MappingUtils.toMap(object),
                null,
                Instant.now(),
                Event.Status.FAILED,
                null,
                null));
        log.error(createMoveEventToFailedMesage(object.getEventId()));
        throw new DomainException(String.format("User creation failed after a few tries for eventId %s", object.getEventId()));
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void deleteUser(String userId, String eventId) {

        throwIfExcluded(eventRepository, eventId);

        eventPublisher.publishEvent(new DeleteUserEvent(
                eventId,
                Map.ofEntries(Map.entry(USER_ID, userId)),
                null,
                Instant.now(),
                Event.Status.FAILED,
                null,
                null));
        log.error(createMoveEventToFailedMesage(eventId));
        throw new DomainException(String.format("User creation failed after a few tries for eventId %s", eventId));
    }

    private String createMoveEventToFailedMesage(String eventId) {

        return String.format("Event %s moved to status FAILED after max number of retries %n", eventId);
    }

}
