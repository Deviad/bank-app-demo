package account.application.dto;

import javax.validation.constraints.NotBlank;

import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
public class UserDeleteCommand {
    @NotBlank
    String userId;
}
