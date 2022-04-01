package account.application.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum TransactionType {

    DEPOSIT,
    WIRE_TRANSFER,
    CREDIT_CARD

}
