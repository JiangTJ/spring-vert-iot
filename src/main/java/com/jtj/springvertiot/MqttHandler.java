package com.jtj.springvertiot;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by jiang (jiang.taojie@foxmail.com)
 * 2018/5/13 22:07 End.
 */
@Service
public class MqttHandler {

    @Resource
    private MqttConfig mqttConfig;

    public String sendMsg(String no, String msg, MqttQoS qos){
        MqttEndpoint endpoint = mqttConfig.getEndpointMap().getOrDefault(no, null);
        if (endpoint == null) {
            return "fail";
        }
        endpoint.publish("Open", Buffer.buffer(msg), qos, false, false);
        // specifing handlers for handling QoS 1 and 2
        endpoint
                .publishAcknowledgeHandler(messageId -> {
                    System.out.println("Received ack for message = " +  messageId);
                })
                .publishReceivedHandler(endpoint::publishRelease)
                .publishCompletionHandler(messageId -> {
                    System.out.println("Received ack for message = " +  messageId);
                });
        return "success";
    }

}
