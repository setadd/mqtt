package com.ffy.mqtt.model;

import lombok.Data;

//https://juejin.cn/post/6844904079274180621
@Data
public class Message {
    Long messageId;
    String playLoad;
}
