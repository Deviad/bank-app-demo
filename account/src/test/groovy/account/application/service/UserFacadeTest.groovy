package account.application.service

import account.application.dto.AddressDto
import account.application.dto.UserCreateCommand
import account.application.messaging.CompletedTransactionListener
import account.application.messaging.TransactionCompletedRequestMessage
import account.application.messaging.TransactionType
import account.application.service.AccountQueryService
import account.application.service.UserFacade
import account.application.service.UserQueryService
import account.domain.AccountRepository
import account.domain.UserRepository
import account.domain.model.AccountType
import groovy.util.logging.Slf4j
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.configuration.kafka.config.KafkaDefaultConfiguration
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import io.micronaut.core.io.socket.SocketUtils
import io.micronaut.http.client.HttpClient
import io.micronaut.messaging.annotation.MessageBody
import io.micronaut.runtime.server.EmbeddedServer
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.errors.TopicExistsException
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.PollingConditions

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

import static io.micronaut.configuration.kafka.annotation.KafkaClient.Acknowledge.ALL
import static io.micronaut.configuration.kafka.config.AbstractKafkaConfiguration.EMBEDDED_TOPICS

@Stepwise
class UserFacadeTest extends Specification {
    static KafkaContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
    {
        container.start()

    }

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            "micronaut.server.port": SocketUtils.findAvailableTcpPort(),
            'spec.name'              : "AccountControllerTest",
            'kafka.bootstrap.servers': container.bootstrapServers,
            (EMBEDDED_TOPICS): ['transaction-completed', 'transaction-started']

    ], "kafka-test")
    @Shared
    @AutoCleanup
    HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)


    def userFacade = embeddedServer.applicationContext.getBean(UserFacade)
    def completedTransactionListener = embeddedServer.applicationContext.getBean(TestCompletedTransactionListener)
    def userQueryService = embeddedServer.applicationContext.getBean(UserQueryService)
    def accountQueryService = embeddedServer.applicationContext.getBean(AccountQueryService)
    def transactionCompletedClient = embeddedServer.applicationContext.getBean(TransactionCompletedClient)
    def tc = embeddedServer.applicationContext.getBean(KafkaTopicCreator)
    def ur = embeddedServer.applicationContext.getBean(UserRepository)

    def cleanup() {
        ur.deleteAll()
    }

    void cleanupSpec() {
        container.stop()
        embeddedServer.stop()
    }

    void setup() {
//        embeddedServer.applicationContext.createBean(TestLivenessHealthIndicator)

    }

    void "test createAccount with deposit complete flow"() {
        given:
        userFacade.addUser(new UserCreateCommand("Davide1", "test1", "test1", "test1",
                new AddressDto("test", "test", "test", "test"),
                AccountType.JUNIOR,
                TransactionType.DEPOSIT,
                100.00d))
        sleep(1000)
        when:
        def initialAccounts = accountQueryService.getAccountsByUsername("test1")
        def message = new TransactionCompletedRequestMessage(100d, initialAccounts[0].id)
//        transactionCompletedClient.sendNotification(initialAccounts[0].id, message)
        completedTransactionListener.updateBalance(initialAccounts[0].id, message)
        then:
        PollingConditions conditions = new PollingConditions(timeout: 30, delay: 1)
        conditions.eventually {
            def finalAccounts = accountQueryService.getAccountsByUsername("test1")
            finalAccounts[0].accountBalance == 100d
        }
    }

    @KafkaClient(id = "transactionCompletedClient", maxBlock = '1s', acks = ALL)
    static interface TransactionCompletedClient {
        @Topic("transaction-completed")
        void sendNotification(@KafkaClient String accountId, @MessageBody TransactionCompletedRequestMessage transaction);
    }


    @Singleton
    @Slf4j
    public static class KafkaTopicCreator {
//        private static final Logger logger = LoggerFactory.getLogger(KafkaTopicCreator.class);
        private final KafkaDefaultConfiguration kafkaConfiguration;

        @Inject
        KafkaTopicCreator(KafkaDefaultConfiguration configuration) {
            this.kafkaConfiguration = configuration;
        }

        public void execute(KafkaTopicConfig topicConfig) {
            AdminClient adminClient = AdminClient.create(this.kafkaConfiguration.getConfig());
            Set<String> topics;
            try {
                topics = adminClient.listTopics().names().get(5000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("Could not get kafka topics by adminclient. The initializer failed", e);
                return;
            }

            try {
                log.info("Checking existence of topic {}", topicConfig.topicName);
                if (topics.contains(topicConfig.topicName)) {
                    return;
                }
                NewTopic newTopic = new NewTopic(topicConfig.topicName, topicConfig.onCreatePartitionNums,
                        (short) topicConfig.onCreateReplicateFactor);
                Map<String, String> topicConfigs = new HashMap<>();
                topicConfigs.put("retention.ms", Long.toString(topicConfig.retentionTimeInMs));
                newTopic.configs(topicConfigs);

                final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));
                createTopicsResult.values().get(topicConfig.topicName).get();
                log.info("{} topic is created with replication factor {} and partition number {} retention time in ms {}",
                        topicConfig.topicName,
                        topicConfig.onCreateReplicateFactor,
                        topicConfig.onCreatePartitionNums,
                        topicConfig.retentionTimeInMs);

            } catch (InterruptedException | ExecutionException e) {
                if (!(e.getCause() instanceof TopicExistsException))
                    throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public static class KafkaTopicConfig {
        public final String topicName;
        public final int onCreatePartitionNums;
        public final int onCreateReplicateFactor;
        public final long retentionTimeInMs;

        public KafkaTopicConfig(String topicName, int onCreatePartitionNums, int onCreateReplicateFactor, long retentionTimeInMs) {
            this.topicName = topicName;
            this.onCreatePartitionNums = onCreatePartitionNums;
            this.onCreateReplicateFactor = onCreateReplicateFactor;
            this.retentionTimeInMs = retentionTimeInMs;
        }
    }

    @Singleton
    static class MyProducerCreateListener implements BeanCreatedEventListener<TransactionCompletedClient> {

        @Inject
        KafkaTopicCreator topicCreator;

        @Override
        TransactionCompletedClient onCreated(BeanCreatedEvent<TransactionCompletedClient> event) {
            this.topicCreator.execute(new KafkaTopicConfig("transaction-started", 1, 1, 86400_000L))
            this.topicCreator.execute(new KafkaTopicConfig("transaction-completed", 1, 1, 86400_000L))
            return event.getBean()
        }
    }

    @Context
    @Replaces(CompletedTransactionListener)
    @Primary
    public static class TestCompletedTransactionListener extends CompletedTransactionListener {
        TestCompletedTransactionListener(AccountRepository accountRepository) {
            super(accountRepository)
        }
    }
}
