package account.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum AccountType {

    JUNIOR("JUNIOR"),
    GOLD("GOLD"),
    PLATINUM("PLATINUM"),
    BASIC("BASIC");

    String value;
}
