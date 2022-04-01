
package account.domain.service.fallback;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import account.application.service.UserFacade;
import account.domain.DomainException;
import account.domain.event.CreateAccountEvent;
import account.domain.event.DeleteAccountEvent;
import account.domain.event.Event;
import account.domain.event.EventEntity;
import account.domain.event.EventRepository;
import account.domain.model.AccountType;
import account.infrastructure.MappingUtils;
import static account.infrastructure.Utils.throwIfExcluded;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.retry.annotation.Fallback;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Fallback(excludes = {ConstraintViolationException.class, NoSuchElementException.class})
@Context
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class AccountServiceFallback {
    public static final String USER_ID = "userId";
    private final ApplicationEventPublisher<Event> eventPublisher;
    private EventRepository eventRepository;

    //This is because when there's an error the connection is closed and a new one must be created
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    // Keeping the signature the same for fallback (retryable)
    public void createAccount(String userId,
                              UserFacade.AccountInformation accountInformation,
                              String eventId) {


        throwIfExcluded(eventRepository, eventId);
        Optional<EventEntity> savedEvent = eventRepository.findById(eventId);
        savedEvent.ifPresent(e -> eventPublisher.publishEvent(new CreateAccountEvent(
                eventId,
                MappingUtils.toMap(e.payload()),
                null,
                Instant.now(),
                Event.Status.FAILED,
                null,
                null
        )));

        log.error(createMoveEventToFailedMesage(eventId));
        throw new DomainException(String.format("Account creation failed after a few tries for eventId %s", eventId));
    }

    @Transactional
    public void deleteAccount(String accountId, String eventId) {

        throwIfExcluded(eventRepository, eventId);
        eventPublisher.publishEvent(new DeleteAccountEvent(
                eventId,
                Map.ofEntries(Map.entry(USER_ID, accountId)),
                null,
                Instant.now(),
                Event.Status.FAILED,
                null,
                null));
        log.error(createMoveEventToFailedMesage(eventId));
        throw new DomainException(String.format("Account creation failed after a few tries for eventId %s", eventId));
    }

    private String createMoveEventToFailedMesage(String eventId) {

        return String.format("Event %s moved to status FAILED after max number of retries %n", eventId);
    }


}
