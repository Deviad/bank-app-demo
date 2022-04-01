package account.application.messaging;

import java.util.Optional;
import java.util.UUID;

import account.domain.model.Transaction;
import account.domain.service.TransactionService;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;

@Requires(notEnv = Environment.TEST)
@KafkaListener
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class TransactionListener {
    TransactionService transactionService;
    TransactionCompletedClient completedTransactionClient;

    @Topic("transaction-started")
    public void processTransaction(TransactionStartedRequestMessage request) {
        Transaction transaction = new Transaction()
                .withId(String.format("transaction-%s", UUID.randomUUID()))
                .withAmount(request.getAmount())
                .withTransactionType(request.getTransactionType())
                .withAccountId(request.getAccountId());

        Optional<Double> balance = transactionService.findSumBalanceByAccountId(transaction.getAccountId());
        Transaction updatedTransactionWithBalance;
        if (balance.isPresent()) {
            double newBalance = balance.get() + transaction.getAmount();
            updatedTransactionWithBalance = transaction.withBalance(newBalance);
        } else {
            if(request.getAmount() < 0) {
                throw new RuntimeException("First deposit cannot be a negative value");
            }
            updatedTransactionWithBalance = transaction.withBalance(request.getAmount());
        }



        Transaction savedTransaction = transactionService.addTransaction(updatedTransactionWithBalance);

        TransactionCompletedRequestMessage completed =
                new TransactionCompletedRequestMessage(
                        savedTransaction.getBalance(),
                        savedTransaction.getAccountId());

        completedTransactionClient.sendNotification(completed).block();

    }
}
