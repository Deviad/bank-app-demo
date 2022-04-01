package account.application.dto;

import javax.validation.constraints.NotBlank;

import account.application.messaging.TransactionType;
import account.domain.model.AccountType;
import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
public class AccountCreateCommand {
    @NotBlank
    String userId;
    AccountType accountType;
    TransactionType transactionType;
    double amount;

}
