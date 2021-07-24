package com.ffy.mqtt.mqtt;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@IntegrationComponentScan
public class MqttSenderConfig {
    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.url}")
    private String url;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Value("${mqtt.defaultTopic}")
    private String defaultTopic;

    @Value("${mqtt.completionTimeout}")
    private int completionTimeout ;   //连接超时

    @Value("${mqtt.willData}")
    private String WILL_DATA ;   //连接超时

    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";

    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT_2 = "mqttOutboundChannel_2";

    /**
     * MQTT连接器选项
     */
    @Bean
    public MqttConnectOptions getSenderMqttConnectOptions(){
        MqttConnectOptions options=new MqttConnectOptions();
        // 设置连接的用户名
        if(!username.trim().equals("")){
            options.setUserName(username);
        }
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        // 设置连接的地址
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
        // 但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。

        options.setWill("willTopic", WILL_DATA.getBytes(), 2, false);

        options.setMaxInflight(1000);
        return options;
    }
    /**
     * MQTT客户端
     */
    @Bean
    public MqttPahoClientFactory senderMqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getSenderMqttConnectOptions());
        return factory;
    }

    //接收通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }



    /**
     * MQTT信息通道（生产者）
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }




    /**
     * MQTT消息处理器（生产者）
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId,
                senderMqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        return messageHandler;
    }



}
