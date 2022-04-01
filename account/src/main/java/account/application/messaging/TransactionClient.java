package account.application.messaging;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import reactor.core.publisher.Mono;

@KafkaClient
public interface TransactionClient {
    @Topic("transaction-started")
    Mono<TransactionStartedRequestMessage> addTransaction(TransactionStartedRequestMessage transaction);
}
