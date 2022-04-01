package account.application.service;

import account.domain.event.Event;
import io.micronaut.retry.annotation.Retryable;
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class SMSSender {

    @Async
    @TransactionalEventListener
    @Retryable
    void onNewEvent(Event event) {

        if (event.status() != Event.Status.COMPLETED) {
            return;
        }

        log.info("Event type: {}, with status: {}. {} Have a nice day!", event.eventType(), event.status(), System.lineSeparator());

    }
}
