package account.application.service;

import java.util.Optional;

import account.application.adapter.AccountAdapter;
import account.application.adapter.UserAdapter;
import account.application.dto.UserResponse;
import account.domain.UserRepository;
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
public class UserQueryService {

    UserRepository userRepository;
    UserAdapter userAdapter;
    AccountAdapter accountAdapter;

    /*
        For this demo app I focused on reliability.
        Obviously if this was for real I would protect these methods so that
        information can be retrieved only by the account owners or employees with sufficient permissions
     */

    @Retryable
    public Optional<UserResponse> getUser(String userId) {

        return userRepository.findById(userId).map(u -> userAdapter.mapToUserResponse(u, accountAdapter.mapListToAccountResponses(u.getAccounts())));
    }

    @Retryable
    public Optional<UserResponse> getUserWithAccounts(String username) {

        return userRepository.getUserWithAccountsByUsername(username)
                .map(u -> userAdapter.mapToUserResponse(u, accountAdapter.mapListToAccountResponses(u.getAccounts())));

    }

}