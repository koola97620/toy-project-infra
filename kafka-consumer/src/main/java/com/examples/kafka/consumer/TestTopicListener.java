package com.examples.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestTopicListener {
    @KafkaListener(topics = "topic1", groupId = "testGroup1")
    public void listenByTestGroupOne(String message) {
        System.out.println("Received Message in testGroup1 : " + message);
    }
}
