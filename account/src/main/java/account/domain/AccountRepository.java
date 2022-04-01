package account.domain;

import account.domain.model.Account;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
}
