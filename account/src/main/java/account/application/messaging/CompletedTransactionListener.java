package account.application.messaging;

import java.util.Optional;

import account.domain.AccountRepository;
import account.domain.model.Account;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Requires(notEnv = Environment.TEST)
@KafkaListener
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class CompletedTransactionListener {
    AccountRepository accountRepository;

    @Topic("transaction-completed")
    public void updateAnalytics(TransactionCompletedRequestMessage transaction) {

        Optional<Account> account = accountRepository.findById(transaction.getAccountId());
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
