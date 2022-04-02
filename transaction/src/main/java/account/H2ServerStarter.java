package account;

import org.h2.tools.Server;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

@Requires(notEnv = Environment.TEST)
@Singleton
public class H2ServerStarter {

    @SneakyThrows
    @EventListener
    void setup(ApplicationStartupEvent event) {
        Server.createWebServer("-webAllowOthers", "-webPort", "8083").start();
    }

}
