package account.application.service;

import java.util.UUID;

import account.application.dto.AccountCreateCommand;
import account.application.dto.AccountDeleteCommand;
import account.application.messaging.TransactionClient;
import account.application.messaging.TransactionStartedRequestMessage;
import account.domain.event.Event;
import account.domain.event.EventType;
import account.domain.service.AccountService;
import account.infrastructure.MappingUtils;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class AccountFacade {

    AccountService accountService;
    TransactionClient transactionClient;

    public void addAccount(AccountCreateCommand command) {

        // I am stuffing the eventId here so that I can use this in the AccountServiceFallback
        accountService.createAccount(command.getUserId(), new UserFacade.AccountInformation(command.getTransactionType(),
                        command.getAmount(), command.getAccountType()),
                String.format("create-account-%s",
                        UUID.randomUUID()));
    }

    public void deleteAccount(AccountDeleteCommand command) {

        accountService.deleteAccount(command.getAccountId(), String.format("create-account-%s", UUID.randomUUID()));
    }


    @EventListener
    void addBalance(Event event) {

        if (EventType.ACCOUNT_CREATE != event.eventType() || event.status() != Event.Status.COMPLETED) {
            return;
        }

        AccountService.CreatedAccountPayload payload = MappingUtils.MAPPER.convertValue(event.payload(), AccountService.CreatedAccountPayload.class);

        if(payload.getInformation().getAmount() == 0) {
            return;
        }

        transactionClient.addTransaction(payload.getAccountId(), new TransactionStartedRequestMessage(payload.getInformation().getAmount(),
                payload.getInformation().getTransactionType(), payload.getAccountId()));
    }

}
