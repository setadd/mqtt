package com.ffy.mqtt.mqtt;

import cn.hutool.json.JSONUtil;
import com.ffy.mqtt.model.Message;
import com.ffy.mqtt.rabbitmq.SimpleSender;
import com.ffy.mqtt.util.DefaultFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttResHandler {

    @Autowired
    SimpleSender simpleSender;

    public void deal(Message msg){
        Long msgId = msg.getMessageId();
        if(DefaultFuture.contains(msgId)){
            DefaultFuture.received(msg);
        }else{
            simpleSender.send(JSONUtil.toJsonStr(msg));
        }
    }
}
