package account;

import org.h2.tools.Server;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.SneakyThrows;

@OpenAPIDefinition(
        info = @Info(
                title = "transaction",
                version = "0.0"
        )
)

public class Application {
    @SneakyThrows
    public static void main(String[] args) {

        Server.createWebServer("-webPort", "8083").start();
        Micronaut.run(Application.class, args);
    }
}
