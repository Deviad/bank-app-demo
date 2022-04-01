package account.domain.service;

import java.util.Optional;

import account.domain.TransactionRepository;
import account.domain.model.Transaction;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class TransactionService {
    TransactionRepository transactionRepository;

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }


    public Optional<Double> findSumBalanceByAccountId(String accountId) {
       return transactionRepository.findSumBalanceByAccountId(accountId);
    }

}
