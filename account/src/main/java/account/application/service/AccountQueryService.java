package account.application.service;

import java.util.Collections;
import java.util.List;

import account.application.adapter.AccountAdapter;
import account.application.adapter.UserAdapter;
import account.application.dto.AccountResponse;
import account.application.dto.UserResponse;
import account.domain.AccountRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
@Getter(AccessLevel.PACKAGE)
public class AccountQueryService {

    AccountRepository accountRepository;
    AccountAdapter accountAdapter;
    UserAdapter userAdapter;

    /*
        For this demo I focused on reliability.
        Obviously if this was for real I would protect these methods so that
        information can be retrieved only by the account owners or employees with sufficient permissions.

        Here depending on the circumstances, we may use a Response Dto or a JSonView to hide some data which
        is not meant to be displayed.
     */

    @Retryable(excludes = HttpStatusException.class)
    public AccountResponse getAccount(String accountId,
                                      String userId) {

       var acc = accountRepository.findById(accountId).orElseThrow();

       //For demo purposes I am passing the userId with header,
       // in real apps I get it from the JWT token.
       if (!acc.getUser().getId().equals(userId)) {
           throw new HttpStatusException(HttpStatus.BAD_REQUEST, "You are not the owner of the account");
       }
       return accountAdapter.mapToAccountResponse(acc);
    }

    @Retryable
    public List<AccountResponse> getAccounts(String userId) {
        return accountRepository.getUserwithAccountsByUserId(userId)
                .map(u -> userAdapter.mapToUserResponse(u, accountAdapter.mapListToAccountResponses(u.getAccounts())))
                .map(UserResponse::getAccounts)
                .orElse(Collections.emptyList());
    }

    @Retryable
    public List<AccountResponse> getAccountsByUsername(String username) {
        return accountRepository.getUserWithAccountsByUsername(username)
                .map(u -> userAdapter.mapToUserResponse(u, accountAdapter.mapListToAccountResponses(u.getAccounts())))
                .map(UserResponse::getAccounts)
                .orElse(Collections.emptyList());
    }
}
