package account.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public enum EventType {

    ACCOUNT_CREATE("ACCOUNT_CREATE"),
    ACCOUNT_DELETE("ACCOUNT_DELETE"),
    USER_CREATE("USER_CREATE"),
    USER_DELETE("USER_DELETE");

    private String getValue;
}
