package account.application.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStartedRequestMessage {

    double amount;
    TransactionType transactionType;
    String accountId;

}
