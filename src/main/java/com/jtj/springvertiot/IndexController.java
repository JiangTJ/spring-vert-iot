package com.jtj.springvertiot;

import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by jiang (jiang.taojie@foxmail.com)
 * 2018/5/13 19:31 End.
 */
@RestController
public class IndexController {

    @Resource
    private MqttHandler handler;

    @GetMapping("/")
    public String index(){
        return "Okay!!";
    }

    @GetMapping("/{no}/{msg}")
    public String send(@PathVariable String no, @PathVariable String msg){
        return handler.sendMsg(no, msg, MqttQoS.EXACTLY_ONCE);
    }

}
