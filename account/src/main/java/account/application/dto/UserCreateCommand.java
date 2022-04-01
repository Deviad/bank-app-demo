package account.application.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import account.application.messaging.TransactionType;
import account.domain.model.AccountType;
import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
public class UserCreateCommand {
    @NotBlank
    String name;
    @NotBlank
    String surname;
    @NotBlank
    String username;
    @NotBlank
    String password;
    @NotNull
    @Valid
    AddressDto address;
    @NotNull
    AccountType accountType;
    @NotNull
    TransactionType transactionType;
    double amount;

}
