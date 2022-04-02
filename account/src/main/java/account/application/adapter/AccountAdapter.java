package account.application.adapter;


import java.util.List;
import java.util.stream.Collectors;

import account.application.dto.AccountResponse;
import account.domain.model.Account;
import jakarta.inject.Singleton;

@Singleton
public class AccountAdapter {

    public List<AccountResponse> mapListToAccountResponses(List<Account> accounts) {

        return accounts.stream().map(
                a -> new AccountResponse(a.getId(), a.getType(), a.getAccountBalance())).collect(Collectors.toList());
    }


    public AccountResponse mapToAccountResponse(Account a) {

        return new AccountResponse(a.getId(), a.getType(), a.getAccountBalance());
    }
}