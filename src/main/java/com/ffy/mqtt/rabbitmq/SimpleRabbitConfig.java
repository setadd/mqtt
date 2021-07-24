package com.ffy.mqtt.rabbitmq;


import com.ffy.mqtt.constant.Constant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SimpleRabbitConfig {

    @Bean
    public Queue mqttResponse() {
        return new Queue(Constant.MQTT_QUEUE);
    }



}
