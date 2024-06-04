package com.ead.authuser.api.publishers;

import com.ead.authuser.domain.dtos.response.UserEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${ead.broker.exchange}")
    private String exchangeUserEvent;

    public void publishUserEvent(UserEventDTO userEventDTO) {
        rabbitTemplate.convertAndSend(exchangeUserEvent, "", userEventDTO);

    }
}

