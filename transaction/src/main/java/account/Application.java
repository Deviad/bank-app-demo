package account;

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

        Micronaut.run(Application.class, args);
    }
}
