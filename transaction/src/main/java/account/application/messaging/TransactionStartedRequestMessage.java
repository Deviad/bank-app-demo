package account.application.messaging;

import account.domain.model.TransactionType;
import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
public class TransactionStartedRequestMessage {

    double amount;
    TransactionType transactionType;
    String accountId;

}
