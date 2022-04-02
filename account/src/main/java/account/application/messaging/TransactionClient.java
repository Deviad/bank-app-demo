package account.application.messaging;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.annotation.Body;
import io.micronaut.messaging.annotation.MessageBody;
import reactor.core.publisher.Mono;

@KafkaClient(id = "transactionStartedClient")
public interface TransactionClient {
    @Topic("transaction-started")
    void addTransaction(@KafkaKey String accountId, @MessageBody TransactionStartedRequestMessage transaction);
}
