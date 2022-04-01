package account.domain;

import java.util.List;
import java.util.Optional;

import account.domain.model.Transaction;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {


        List<Transaction> findFirstByAccountIdOrderByCreatedAtDesc(String accountId);

        Optional<Double> findSumBalanceByAccountId(String accountId);

}
