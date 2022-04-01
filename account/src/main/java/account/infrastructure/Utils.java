package account.infrastructure;

import javax.transaction.Transactional;
import java.util.function.Consumer;

import account.domain.event.EventEntity;
import account.domain.event.EventRepository;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    @SneakyThrows
    public static String serializeException(Exception ex) {

        return MappingUtils.MAPPER.writeValueAsString(ex);
    }

    @SneakyThrows
    public static void throwIfExcluded(EventRepository eventRepository,
                                       String eventId) {

    /*
        This is a temporary fix until https://github.com/micronaut-projects/micronaut-core/pull/7153
        is merged
     */

        var ev = eventRepository.findById(eventId).orElseThrow();

        String exType = ev.getExceptionType();
        String serEx = ev.getException();

        var cl = Class.forName(exType);
        if (exType.contains("ConstraintViolationException") ||
                exType.contains("NoSuchElementException") ||
                exType.contains("PersistenceException")) {
            throw (Exception) MappingUtils.MAPPER.readValue(serEx, cl);
        }

    }

}
