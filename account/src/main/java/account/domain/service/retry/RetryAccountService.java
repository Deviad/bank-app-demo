package account.domain.service.retry;


import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


import account.domain.event.CreateAccountEvent;
import account.domain.event.DeleteAccountEvent;
import account.domain.event.Event;
import account.domain.event.EventType;
import account.domain.model.Account;
import account.domain.service.AccountService;
import account.infrastructure.MappingUtils;
import io.micronaut.context.annotation.Context;
import io.micronaut.retry.annotation.Retryable;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Context
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class RetryAccountService {
    AccountService accountService;


    @EventListener
    void retry(Event event) {

        if (!(event instanceof CreateAccountEvent) && !(event instanceof DeleteAccountEvent)) {
            return;
        }
        if (event.eventType() == EventType.ACCOUNT_CREATE && event.status() == Event.Status.RETRY) {
        Account account = MappingUtils.MAPPER.convertValue(event.payload(), Account.class);
            retryCreateAccount(account, event.eventId());
        } else if (event.eventType() == EventType.ACCOUNT_DELETE && event.status() == Event.Status.RETRY) {

            retryDeleteAccount((String)event.payload().get("accountId"), event.eventId());
        }
    }

    @Transactional
    @Retryable
    void retryCreateAccount(Account account,
                                    String eventId) {

        try {
            accountService.getAccountRepository().save(account);
            accountService.getEventPublisher().publishEvent(new CreateAccountEvent(
                    eventId,
                    Collections.emptyMap(),
                    null,
                    Instant.now(),
                    Event.Status.COMPLETED,
                    null,
                    null));
        } catch (NoSuchElementException ex) {
            log.error("Account creation definitively failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Account creation definitively failed for eventId {}, accountId {}  with reason: %s",
                    eventId, Optional.of(account).map(Account::getId).orElse(""), ex);
        }
    }

    @Transactional
    @Retryable
    void retryDeleteAccount(String accountId, String eventId) {
        Optional<Account> account = Optional.empty();
        try {
            account = accountService.getAccountRepository().findById(accountId);
            accountService.getAccountRepository().delete(account.orElseThrow());
            accountService.getEventPublisher().publishEvent(new CreateAccountEvent(
                    eventId,
                    Collections.emptyMap(),
                    null,
                    Instant.now(),
                    Event.Status.COMPLETED,
                    null,
                    null));
        } catch(NoSuchElementException ex) {
            log.error("Account deletion failed for eventId {}  with reason: %s", eventId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Account deletion failed for eventId {} and accountId {} with reason: %s",
                    eventId,
                    account.map(Account::getId).orElse(""),
                    ex);
        }
    }
}

