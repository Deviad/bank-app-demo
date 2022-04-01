package account;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import lombok.SneakyThrows;

import org.h2.tools.Server;

@OpenAPIDefinition(
    info = @Info(
            title = "bankapp",
            version = "0.0"
    )
)
public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        Server.createWebServer().start();
        Micronaut.run(Application.class, args);
    }
}
