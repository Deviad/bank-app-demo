package account.application.service;

import java.util.UUID;

import account.application.adapter.UserAdapter;
import account.application.dto.UserCreateCommand;
import account.application.messaging.TransactionType;
import account.domain.model.AccountType;
import account.domain.model.User;
import account.domain.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Value;

@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class UserFacade {
    UserAdapter userAdapter;
    UserService userService;
    AccountFacade accountFacade;

    public void addUser(UserCreateCommand userCommand) {
        var user = userAdapter.toEntity(userCommand);
        var param = new CreateUserParameterObject(user, new AccountInformation(userCommand.getTransactionType(),
                userCommand.getAmount(), userCommand.getAccountType()),  String.format("delete-user-%s", UUID.randomUUID()));

        // I am stuffing the eventId here so that I can use this in the UserServiceFallback
        userService.createUser(param);
    }


    public void deleteUser(String userId) {
        userService.deleteUser(userId,  String.format("delete-user-%s", UUID.randomUUID()));
    }

    @Value
    @Introspected
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateUserParameterObject {
        User user;
        AccountInformation accountInfo;
        String eventId;
    }

    @Value
    @Introspected
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccountInformation {
        TransactionType transactionType;
        double amount;
        AccountType accountType;
    }

}
