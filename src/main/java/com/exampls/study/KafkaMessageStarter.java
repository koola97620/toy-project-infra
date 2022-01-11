package com.exampls.study;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class KafkaMessageStarter {

    private final MessagePublisher messagePublisher;

    public KafkaMessageStarter(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @PostConstruct
    public void start() {
        while (true) {
            System.out.print("Input message : ");
            String input = MessageInserter.input();
            messagePublisher.sendMessage(input);
            System.out.println();
        }
    }
}
