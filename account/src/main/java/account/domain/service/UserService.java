package account.domain.service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import account.application.service.UserFacade;
import account.domain.UserRepository;
import account.domain.event.CreateUserEvent;
import account.domain.event.DeleteUserEvent;
import account.domain.event.Event;
import account.domain.event.EventEntity;
import account.domain.event.EventRepository;
import account.domain.model.User;
import account.domain.service.fallback.UserServiceFallback;
import account.infrastructure.MappingUtils;
import static account.infrastructure.Utils.serializeException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.retry.annotation.Recoverable;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Recoverable(api = UserServiceFallback.class)
@Getter
@Slf4j
public class UserService {
    private static final String USER_ID = "userId";
    UserRepository userRepository;
    private final ApplicationEventPublisher<Event> eventPublisher;
    private final EventRepository eventRepository;

    @Retryable(excludes = {ConstraintViolationException.class, NoSuchElementException.class, SQLException.class})
    @Transactional
    public void createUser(UserFacade.CreateUserParameterObject object) {

        User u = User.create(object.getUser());
        try {
            //I save the payload also upon reception, and not just on fail, because if there are issues like a power outage,
            // the action would be stalled without any reover possibility

            Instant createdAt = Instant.now();
            eventPublisher.publishEvent(new CreateUserEvent(
                    object.getEventId(),
                    MappingUtils.toMap(object),
                    createdAt,
                    null,
                    Event.Status.RECEIVED,
                    null,
                    null));
            userRepository.save(u);
            eventPublisher.publishEvent(
                    new CreateUserEvent(
                            object.getEventId(),
                            MappingUtils.toMap(new CompletedUserCreatePayload(object, u.getId())),
                            createdAt,
                            Instant.now(),
                    Event.Status.COMPLETED,
                            null,
                            null));
        } catch (ConstraintViolationException ex) {
            saveException(eventRepository, object.getEventId(), ex);
            log.error("User creation failed for eventId {}  with reason: {}", object.getEventId(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("User creation failed for eventId {}, userId {}  with reason: %s",
                    object.getEventId(), Optional.of(u).map(User::getId).orElse(""), ex);
            throw ex;
        }
    }



    @Retryable(excludes = {ConstraintViolationException.class, SQLException.class, PersistenceException.class})
    @Transactional
    public void deleteUser(String userId, String eventId) {
        try {
            //I save the payload also upon reception, and not just on fail, because if there are issues like a power outage,
            // the action would be stalled without any reover possibility

            eventPublisher.publishEvent(new DeleteUserEvent(
                    eventId,
                    Map.ofEntries(Map.entry(USER_ID, userId)),
                    Instant.now(),
                    null,
                    Event.Status.RECEIVED,
                    null,
                    null));
            userRepository.deleteById(userId);
            eventPublisher.publishEvent(new DeleteUserEvent(eventId, Collections.emptyMap(), null, Instant.now(), Event.Status.COMPLETED,
             null, null ));
        }  catch (ConstraintViolationException | PersistenceException ex) {
            saveException(eventRepository, eventId, ex);
            log.error("User deletion failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("User deletion failed for eventId {}, userId {}  with reason: %s",
                    eventId, userId, ex);
            throw ex;
        }
    }
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void saveException(EventRepository eventRepository,
                       String eventId,
                       Exception ex) {

        Consumer<EventEntity> eventEntityConsumer = ev -> {
            var newE = ev
                    .withExceptionType(ex.getClass().getTypeName())
                    .withException(serializeException(ex));
            eventRepository.update(newE);
        };
        eventRepository.findById(eventId).ifPresent(eventEntityConsumer);
    }

    @Value
    @Introspected
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompletedUserCreatePayload {
        UserFacade.CreateUserParameterObject paramObj;
        String userId;
    }
}
