package account.domain;

import account.domain.model.Account;
import account.domain.model.User;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {


        @Query("select u from User u left join fetch u.accounts where u.id = :userId")
        User getUserwithAccountsByUserId(String userId);

        @Query("select u from User u left join fetch u.accounts where u.username = :username")
        User getUserWithAccountsByUsername(String username);

}
