package account.domain;

import java.util.Optional;

import account.domain.model.User;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {


    @Query("select u from User u left join fetch u.accounts where u.id = :userId")
    Optional<User> getUserwithAccountsByUserId(String userId);

    @Query("select u from User u left join fetch u.accounts where u.username = :username")
    Optional<User> getUserWithAccountsByUsername(String username);

}
