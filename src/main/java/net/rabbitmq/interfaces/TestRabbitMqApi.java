package net.rabbitmq.interfaces;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.rabbitmq.common.TestMqMessage;
import net.rabbitmq.config.RabbitConsts;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Author: tianjl
 * @Date: 2020/8/25 16:04
 * @Eamil: 2695062879@qq.com
 */
@RestController
@RequestMapping(value = "/test")
public class TestRabbitMqApi {

    /**
     * mq服务
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping(value = "/rabbit/{str}")
    public void test(@PathVariable String str){
       sendMessage2Lab(str);
    }


    private void sendMessage2Lab(String str) {
        TestMqMessage labServiceResponse=new TestMqMessage();
        labServiceResponse.setMessage(str);
        labServiceResponse.setMoney("100000$");
        byte[] body = JSON.toJSONBytes(labServiceResponse, SerializeConfig.globalInstance);
        //设置消息相关属性
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        messageProperties.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Message rabbitmqMessage = new Message(body, messageProperties);
        rabbitTemplate.convertAndSend(RabbitConsts.FANOUTEXCHANGE,"fount.*", rabbitmqMessage);
    }
}
