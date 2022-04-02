package account.application.web

import account.TestSuiteSpecification
import account.domain.UserRepository
import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.socket.SocketUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Flux
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class UserControllerTest extends TestSuiteSpecification {
    static KafkaContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
    {
        container.start()
    }

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            "micronaut.server.port": SocketUtils.findAvailableTcpPort(),
            'spec.name'              : "UserControllerTest",
            'kafka.bootstrap.servers': container.bootstrapServers,

    ])

    @Shared
    @AutoCleanup
    HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    def ur = embeddedServer.applicationContext.getBean(UserRepository)

    def cleanup() {
        ur.deleteAll()
    }


    void cleanupSpec() {
        container.stop()
        embeddedServer.stop()
    }



    void "test createUser sunny day scenario"() {
        given:
        def payload = [
                "name"           : "Davide",
                "surname"        : "test",
                "username"       :  "customname",
                "password"       : "test",
                "address"        : [
                        "city"    : "test",
                        "province": "test",
                        "street"  : "test",
                        "country" : "test"
                ],
                "accountType"    : "JUNIOR",
                "transactionType": "DEPOSIT",
                "amount"         : 0
        ]
        when:
        HttpResponse<String> resp = Flux.from(rxClient
                .exchange(HttpRequest.POST('/user', payload)
                        .contentType(MediaType.APPLICATION_JSON), String))
                .blockFirst()
        then:
        resp.getStatus() == HttpStatus.OK

    }

    void "test createUser rainy day scenario"() {
        given:
        def payload = [
                "name"           : name,
                "surname"        : "test",
                "username"       : username,
                "password"       : password,
                "address"        : [
                        "city"    : "test",
                        "province": "test",
                        "street"  : "test",
                        "country" : "test"
                ],
                "accountType"    : "JUNIOR",
                "transactionType": "DEPOSIT",
                "amount"         : 0
        ]
        when:
        HttpResponse<String> resp = Flux.from(rxClient
                .exchange(HttpRequest.POST('/user', payload)
                        .contentType(MediaType.APPLICATION_JSON), String))
                .blockFirst()
        then:
        def ex = thrown(HttpClientResponseException)
        ex.response.status == respStatus

        if (ex.response.body.isPresent()) {
            assert ex.response.body.toString().contains("Constraint Violation")
        }

        where:
        username | name   | password   | respStatus
        null     | "test" | "password" | HttpStatus.BAD_REQUEST
        ""       | "test" | "password" | HttpStatus.BAD_REQUEST
        "user1"  | ""     | "password" | HttpStatus.BAD_REQUEST
        "user2"  | null   | "password" | HttpStatus.BAD_REQUEST
        "user3"  | "test" | null       | HttpStatus.BAD_REQUEST
        "user4"  | "test" | ""         | HttpStatus.BAD_REQUEST
    }
}