package com.ffy.mqtt.mqtt;

import cn.hutool.json.JSONUtil;
import com.ffy.mqtt.model.Message;
import com.ffy.mqtt.rabbitmq.SimpleSender;
import com.ffy.mqtt.util.DefaultFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttResHandler {


    public void deal(Message msg){
        //根据messageId判断是否是本机发送的消息
        Long msgId = msg.getMessageId();
        if(DefaultFuture.contains(msgId)){
            DefaultFuture.received(msg);
        }
    }
}
