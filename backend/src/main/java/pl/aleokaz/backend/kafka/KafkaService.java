package pl.aleokaz.backend.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import pl.aleokaz.backend.user.User;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, User to, String message) {
        kafkaTemplate.send(topic, to.id().toString(), message);
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    //przykładowy listener
    @KafkaListener(topics = "notification", groupId = "aleokaz")
    public void listen(String message, @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        System.out.println("Received message: " + message + " Key:" + key);
    }
}
