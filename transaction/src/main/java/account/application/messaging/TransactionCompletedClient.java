package account.application.messaging;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageBody;

@KafkaClient(id = "transactionCompletedClient")
public interface TransactionCompletedClient {
    @Topic("transaction-completed")
    void sendNotification(@KafkaKey String accountId, @MessageBody TransactionCompletedRequestMessage transaction);
}
