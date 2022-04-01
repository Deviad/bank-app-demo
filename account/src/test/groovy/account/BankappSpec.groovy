package account

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification
import jakarta.inject.Inject
import spock.util.concurrent.BlockingVariable

@MicronautTest
class BankappSpec extends Specification {

    @Inject
    EmbeddedApplication<?> application

    void 'test it works'() {
        expect:
        application.running
    }

}
