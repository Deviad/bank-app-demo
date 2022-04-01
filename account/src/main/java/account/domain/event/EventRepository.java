package account.domain.event;

import java.util.List;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, String> {

//    @Query(nativeQuery = true,
//            value = "SELECT * FROM EVENT_ENTITY as e WHERE DATEDIFF(MINUTE, e.at, CURRENT_TIMESTAMP) > 10 AND e.STATUS = 'RECEIVED'")
//    List<EventEntity> getUnfinishedEvents(Event.Status status);

    @Query("select e from EventEntity e where e.isRetryable = true and e.retryAttempts = 0 and e.status = 'FAILED'")
    List<EventEntity> getRetryableFailedEvents();

}
