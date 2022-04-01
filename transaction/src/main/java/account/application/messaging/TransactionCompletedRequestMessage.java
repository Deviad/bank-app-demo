package account.application.messaging;

import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
public class TransactionCompletedRequestMessage {

    double balance;
    String accountId;

}
