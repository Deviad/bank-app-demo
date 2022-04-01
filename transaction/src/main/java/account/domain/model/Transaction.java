package account.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.Instant;

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
public class Transaction {
    @Id
    String id;

    @NotNull
    String accountId;

    double amount;

    double balance;

    @NotNull
    TransactionType transactionType;

    Instant createdAt;

}
