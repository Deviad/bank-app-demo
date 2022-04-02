package account.application.messaging;

import java.util.Optional;

import account.domain.AccountRepository;
import account.domain.model.Account;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Requires(notEnv = Environment.TEST)
@KafkaListener(producerClientId = "transactionCompletedClient")
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Context
public class CompletedTransactionListener {
    AccountRepository accountRepository;

    @Topic("transaction-completed")
    public void updateBalance(@KafkaKey String accountId,
                              @MessageBody TransactionCompletedRequestMessage transaction) {

        Optional<Account> account = accountRepository.findById(accountId);
        if (isNotPresent(account)) {
            log.error("Account {} does not exist", transaction.getAccountId());
            return;
        }

        account.ifPresent(ac -> saveNewBalance(transaction, ac));
    }

    private void saveNewBalance(TransactionCompletedRequestMessage transaction,
                           Account ac) {

        var accountWithUpdatedBalance  = ac.withAccountBalance(transaction.getBalance());
        accountRepository.update(accountWithUpdatedBalance);
    }

    private boolean isNotPresent(Optional<Account> account) {

        return account.isEmpty();
    }
}
