package account.application.service;

import account.domain.event.Event;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class SMSSender {

    @EventListener
    void onNewEvent(Event event) {

        if (event.status() != Event.Status.COMPLETED) {
            return;
        }

        log.info("Event type: {}, with status: {}. {} Have a nice day!", event.eventType(), event.status(), System.lineSeparator());

    }
}
