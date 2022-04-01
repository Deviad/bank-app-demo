package account.application.service;

import account.domain.UserRepository;
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
public class UserQueryService {

    UserRepository userRepository;

    /*
        For this demo app I focused on reliability.
        Obviously if this was for real I would protect these methods so that
        information can be retrieved only by the account owners or employees with sufficient permissions
     */

    @Retryable
    public User getUser(String userId) {

       var user = userRepository.findById(userId).orElseThrow();

       //For demo purposes I am passing the userId with header,
       // in real apps I get it from the JWT token.
       if (!user.getId().equals(userId)) {
           throw new HttpStatusException(HttpStatus.BAD_REQUEST, "You are not the owner of this account");
       }
       return user;
    }

}
