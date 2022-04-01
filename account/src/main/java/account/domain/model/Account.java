package account.domain.model;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class Account {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    double accountBalance;

    @ManyToOne
    private User user;


    public static Account create(AccountType accountType,
                                 User user) {
        Objects.requireNonNull(accountType);
        Objects.requireNonNull(user);
        String accountId = String.format("account-%s", UUID.randomUUID());
        return new Account(accountId, accountType, 0, user);
    }

}
