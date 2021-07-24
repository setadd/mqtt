package com.ffy.mqtt.rabbitmq;


import com.ffy.mqtt.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleSender {
    @Autowired
    private RabbitTemplate template;

    public void send(String message) {
        this.template.convertAndSend(Constant.MQTT_QUEUE, message);
        log.info("send_to_rabbitmq:", message);
    }
}
