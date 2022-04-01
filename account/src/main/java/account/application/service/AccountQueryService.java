package account.application.service;

import java.util.List;
import java.util.stream.Collectors;

import account.domain.AccountRepository;
import account.domain.UserRepository;
import account.domain.model.Account;
import account.domain.model.User;
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
    UserRepository userRepository;

    /*
        For this demo I focused on reliability.
        Obviously if this was for real I would protect these methods so that
        information can be retrieved only by the account owners or employees with sufficient permissions
     */

    @Retryable(excludes = HttpStatusException.class)
    public Account getAccount(String accountId,
                              String userId) {

       var acc = accountRepository.findById(accountId).orElseThrow();

       //For demo purposes I am passing the userId with header,
       // in real apps I get it from the JWT token.
       if (!acc.getUser().getId().equals(userId)) {
           throw new HttpStatusException(HttpStatus.BAD_REQUEST, "You are not the owner of the account");
       }
       return acc;
    }

    @Retryable
    public List<List<Account>> getAccounts(String userId) {
        return userRepository.findById(userId)
                .stream()
                .map(User::getAccounts)
                .collect(Collectors.toList());
    }
}
