package com.ffy.mqtt.web;

import com.ffy.mqtt.model.Message;
import com.ffy.mqtt.mqtt.SynMqttSender;
import com.ffy.mqtt.util.DefaultFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mqtt")
public class TestController {
   @Autowired
   SynMqttSender synMqttSender;
    @PostMapping("send")
    public Message sendMsg(@RequestBody  Message message){
        DefaultFuture future = synMqttSender.sendMessage(message);
        return future.get();
    }
}
