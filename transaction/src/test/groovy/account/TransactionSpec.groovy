package account

import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.socket.SocketUtils
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

class TransactionSpec extends Specification {

    def "runs without errors"() {
        given:

        EmbeddedServer embeddedServer = ApplicationContext.builder()
        .properties(["micronaut.server.port": SocketUtils.findAvailableTcpPort()])
        .run(EmbeddedServer)

        when:
        embeddedServer.start()
        then:
        noExceptionThrown()
        embeddedServer.stop()
    }

}
