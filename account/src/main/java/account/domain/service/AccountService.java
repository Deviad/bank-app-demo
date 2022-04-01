package account.domain.service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import account.application.service.UserFacade;
import account.domain.AccountRepository;
import account.domain.UserRepository;
import account.domain.event.CreateAccountEvent;
import account.domain.event.Event;
import account.domain.event.EventEntity;
import account.domain.event.EventRepository;
import account.domain.event.EventType;
import account.domain.model.Account;
import account.domain.model.User;
import account.domain.service.fallback.AccountServiceFallback;
import account.infrastructure.MappingUtils;
import static account.infrastructure.Utils.serializeException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;
import io.micronaut.retry.annotation.Recoverable;
import io.micronaut.retry.annotation.Retryable;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Recoverable(api = AccountServiceFallback.class)
@Slf4j
@Getter
public class AccountService {

    AccountRepository accountRepository;
    UserRepository userRepository;
    EventRepository eventRepository;
    ApplicationEventPublisher<Event> eventPublisher;

    @Transactional
    @Retryable(excludes = {NoSuchElementException.class, ConstraintViolationException.class, SQLException.class})
    public void createAccount(String userId,
                              UserFacade.AccountInformation accountInformation,
                              String eventId) {


        Optional<Account> account = Optional.empty();
        try {
            Instant createdAt = Instant.now();
            eventPublisher.publishEvent(new CreateAccountEvent(eventId, Collections.emptyMap(), createdAt, null,
                    Event.Status.RECEIVED, null, null));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException(String.format("User with id %s does not exist", userId)));

            account = Optional.of(Account.create(accountInformation.getAccountType(), user));
            eventPublisher.publishEvent(new CreateAccountEvent(eventId, MappingUtils.toMap(account), createdAt, Instant.now(),
                    Event.Status.ENTITY_CREATED, null, null));
            Account savedAccount = accountRepository.save(account.orElseThrow());

            eventPublisher.publishEvent(new CreateAccountEvent(
                    eventId,
                    MappingUtils.toMap(new CreatedAccountPayload(accountInformation, savedAccount.getId())),
                    createdAt,
                    Instant.now(),
                    Event.Status.COMPLETED, null, null));
        } catch (ConstraintViolationException | NoSuchElementException ex) {
            saveException(eventRepository, eventId, ex);
            log.error("Account creation failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Account creation failed for eventId {}, accountId {}  with reason: %s",
                    eventId, account.map(Account::getId).orElse(""), ex);
        }
    }


    @Transactional
    @Retryable(excludes = {ConstraintViolationException.class, NoSuchElementException.class, SQLException.class})
    public void deleteAccount(String accountId,
                              String eventId) {

        if (StringUtils.isEmpty(accountId)) {
            throw new BadRequestException("userId cannot be null or empty");
        }
        Account account;
        try {
            eventPublisher.publishEvent(new CreateAccountEvent(
                    eventId,
                    Map.ofEntries(Map.entry("accountId", accountId)),
                    Instant.now(),
                    null,
                    Event.Status.RECEIVED,
                    null,
                    null));
            account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NoSuchElementException(String.format("Account with id %s does not exist", accountId)));
            accountRepository.delete(account);
            eventPublisher.publishEvent(new CreateAccountEvent(
                    eventId,
                    Collections.emptyMap(),
                    null,
                    Instant.now(),
                    Event.Status.COMPLETED,
                    null,
                    null));

        } catch (ConstraintViolationException | NoSuchElementException ex) {
            saveException(eventRepository, eventId, ex);
            log.error("Account deletion failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Account deletion failed for eventId {}, accountId {}  with reason: %s", accountId, eventId, ex);
            throw ex;
        }
    }

    @EventListener
    void listenToEvents(Event event) {

        if (event.eventType() != EventType.USER_CREATE || event.status() != Event.Status.COMPLETED) {
            return;
        }
        var payload = MappingUtils.MAPPER.convertValue(event.payload(), UserService.CompletedUserCreatePayload.class);
        var accInfo = payload.getParamObj().getAccountInfo();
        createAccount(payload.getUserId(), accInfo, String.format("create-account-%s", UUID.randomUUID()));
    }

    /*
        This is a temporary fix until https://github.com/micronaut-projects/micronaut-core/pull/7153
        is merged
 */
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
    public static class CreatedAccountPayload {
        UserFacade.AccountInformation information;
        String accountId;
    }
}
