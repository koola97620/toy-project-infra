package com.exampls.study;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Profile("!local")
@Component
public class KafkaMessageStarter {

    private final MessagePublisher messagePublisher;

    public KafkaMessageStarter(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @PostConstruct
    public void start() {
        messagePublisher.sendMessage("My First Message");
    }
}
