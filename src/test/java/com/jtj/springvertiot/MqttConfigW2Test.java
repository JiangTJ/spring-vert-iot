package com.jtj.springvertiot;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by jiang (jiang.taojie@foxmail.com)
 * 2018/5/13 19:51 End.
 */
public class MqttConfigW2Test {

    @Test
    public void testMqtt(){
        Vertx vertx = Vertx.vertx();
        MqttClient client = MqttClient.create(vertx);
        client.connect(1883, "localhost", s -> {

            //发布注册消息
            client.publish("sign",
                    Buffer.buffer("W2"),
                    MqttQoS.AT_LEAST_ONCE,
                    false,
                    false);

            //订阅主题
            client.publishHandler(message -> {
                System.out.println("There are new message in topic: " + message.topicName());
                System.out.println("Content(as string) of the message: " + message.payload().toString());
                System.out.println("QoS: " + message.qosLevel());
            }).subscribe("Open", MqttQoS.EXACTLY_ONCE.value());

        });
        //阻塞等待
        LockSupport.park();
        if (client.isConnected()) {
            client.disconnect();
        }
    }
}