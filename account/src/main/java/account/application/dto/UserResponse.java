package account.application.dto;


import java.util.ArrayList;
import java.util.List;

import account.domain.model.Address;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
public class UserResponse {
    String id;
    String name;
    String surname;
    @Builder.Default
    List<AccountResponse> accounts = new ArrayList<>();
    Address address;
    String username;
    String password;
}
