package com.ffy.mqtt.rabbitmq;

import cn.hutool.json.JSONUtil;

import com.ffy.mqtt.constant.Constant;
import com.ffy.mqtt.model.Message;
import com.ffy.mqtt.util.DefaultFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RabbitListener(queues = Constant.MQTT_QUEUE)
@Slf4j
@Component
public class SimpleReceiver {
    @RabbitHandler
    public void receive(String in) {
        Message msg = JSONUtil.toBean(in,  Message.class);
       if(DefaultFuture.contains(msg.getMessageId())) {
           DefaultFuture.received(msg);
       }
    }
}
