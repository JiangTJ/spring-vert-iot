package com.jtj.springvertiot;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiang (jiang.taojie@foxmail.com)
 * 2018/5/13 19:35 End.
 */
@Slf4j
@Configuration
public class MqttConfig {

    private MqttServer mqttServer;
    private ConcurrentHashMap<String,MqttEndpoint> endpointMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void startVertMqtt(){
        mqttServer = MqttServer.create(Vertx.vertx());
        mqttServer.endpointHandler(endpoint -> {

            //连接
            log.info("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
            log.info("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

            //处理消息
            endpoint.publishHandler(message -> {
                String payload = message.payload().toString(Charset.defaultCharset());
                log.info("Just received message [" + payload + "] with QoS [" + message.qosLevel() + "]");
                if ("sign".equals(message.topicName())){
                    endpointMap.put(payload.toLowerCase(), endpoint);
                    log.info(payload + " Sign success!");
                }
                if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                    endpoint.publishAcknowledge(message.messageId());
                } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                    endpoint.publishReceived(message.messageId());
                }
            }).publishReleaseHandler(endpoint::publishComplete);

            //处理订阅
            endpoint.subscribeHandler(subscribe -> {
                List<MqttQoS> grantedQosLevels = new ArrayList<>();
                for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
                    log.info("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
                    grantedQosLevels.add(s.qualityOfService());
                }
                //回复订阅
                endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
            });

            //accept connection from the remote client, sessionPresent false
            endpoint.accept(false);

        }).listen(ar -> {
            if (ar.succeeded()) {
                log.info("MQTT server is listening on port " + ar.result().actualPort());
            } else {
                log.info("Error on starting the server");
                ar.cause().printStackTrace();
            }
        });
    }

    @PreDestroy
    public void cleanUp(){
        if (mqttServer != null){
            mqttServer.close(v -> {
                log.info("MQTT server closed");
            });
        }
    }

    public MqttServer getMqttServer() {
        return mqttServer;
    }

    public ConcurrentHashMap<String, MqttEndpoint> getEndpointMap() {
        return endpointMap;
    }
}
