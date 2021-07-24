package com.ffy.mqtt.mqtt;

import cn.hutool.json.JSONUtil;
import com.ffy.mqtt.constant.Constant;
import com.ffy.mqtt.model.Message;
import com.ffy.mqtt.util.DefaultFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SynMqttSender {
    @Autowired
    IMqttSender iMqttSender;
    public DefaultFuture sendMessage(Message msg)  {
        Long msgId = DefaultFuture.generateId();
        msg.setMessageId(msgId);
        iMqttSender.sendToMqtt(Constant.MQTT_TOPIC_REQ,1, JSONUtil.toJsonStr(msg));
        DefaultFuture future = new DefaultFuture(msg.getMessageId(),10);
        return future;
    }
}
