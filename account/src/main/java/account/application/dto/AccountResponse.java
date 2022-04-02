package account.application.dto;


import account.domain.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;

@Value
@Builder
@AllArgsConstructor
public class AccountResponse {

    String id;

    AccountType type;

    double accountBalance;

}
