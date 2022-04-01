package account.domain.service.retry;


import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import account.application.service.UserFacade;
import account.domain.DomainException;
import account.domain.event.CreateAccountEvent;
import account.domain.event.CreateUserEvent;
import account.domain.event.DeleteAccountEvent;
import account.domain.event.DeleteUserEvent;
import account.domain.event.Event;
import account.domain.event.EventType;
import account.domain.model.Account;
import account.domain.model.User;
import account.domain.service.UserService;
import account.infrastructure.MappingUtils;
import io.micronaut.context.annotation.Context;
import io.micronaut.retry.annotation.Retryable;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Context
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class RetryUserService {
    UserService userService;


    @EventListener
    void retry(Event event) {

        if (!(event instanceof CreateUserEvent) && !(event instanceof DeleteUserEvent)) {
            return;
        }

        if (event.eventType() == EventType.USER_CREATE && event.status() == Event.Status.RETRY) {
            UserFacade.CreateUserParameterObject user = MappingUtils.MAPPER.convertValue(event.payload(), UserFacade.CreateUserParameterObject.class);
            retryCreateUser(user.getUser(), event.eventId());
        } else if (event.eventType() == EventType.USER_DELETE && event.status() == Event.Status.RETRY) {
            var userId = (String)event.payload().get("userId");
            retryDeleteUser(userId, event.eventId());
        }
    }

    @Transactional
    @Retryable
    void retryCreateUser(User user,
                         String eventId) {

        try {
            userService.getUserRepository().save(user);
            userService.getEventPublisher().publishEvent(new CreateUserEvent(
                    eventId,
                    MappingUtils.toMap(user),
                    null,
                    Instant.now(),
                    Event.Status.COMPLETED,
                    null,
                    null));
        }  catch (NoSuchElementException ex) {
            log.error("User creation failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("User creation failed for eventId {}, accountId {}  with reason: %s",
                    eventId, Optional.of(user).map(User::getId).orElse(""), ex);
        }
    }

    @Transactional
    @Retryable
    void retryDeleteUser(String userId, String eventId) {
        Optional<User> account = Optional.empty();
        try {
            account = userService.getUserRepository().findById(userId);
            userService.getUserRepository().delete(account.orElseThrow());
            userService.getEventPublisher().publishEvent(new DeleteUserEvent(
                    eventId,
                    Collections.emptyMap(),
                    null,
                    Instant.now(),
                    Event.Status.COMPLETED,
                    null,
                    null));
        } catch(ConstraintViolationException ex) {
            log.error("User deletion failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("User creation failed for eventId {} and accountId {} with reason: %s", eventId, userId, ex);
            throw new DomainException(ex);
        }
    }



}

