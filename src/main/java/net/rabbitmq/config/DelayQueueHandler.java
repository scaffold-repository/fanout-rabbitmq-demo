package net.rabbitmq.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * <p>
 * 延迟队列处理器
 * </p>
 *
 * @package: com.xkcoding.mq.rabbitmq.handler
 * @description: 延迟队列处理器
 * @author: yangkai.shen
 * @date: Created in 2019-01-04 17:42
 * @copyright: Copyright (c) 2019
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Slf4j
@Component
public class DelayQueueHandler {


    /**
     * 消息队列监听
     * @param message
     * @param channel
     */
    @RabbitListener(queues = RabbitConsts.FANOUT155)
    public void directHandlerManualAck155(Message message, Channel channel) {
        //  如果手动ACK,消息会被监听消费,但是消息在队列中依旧存在,如果 未配置 acknowledge-mode 默认是会在消费完毕后自动ACK掉
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] str=message.getBody();
            Object a= JSONArray.parse(str);
            String object=JSON.parseObject(a.toString(),String.class);
            log.info("接收消息：{}", object);
            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                log.error("ACK失败！"+e.toString());
                // 处理失败,重新压入MQ
                channel.basicRecover();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转化来自.net程序的转化任务
     * @param a
     * @return
     */
    private String doParse(String a) {
        return JSON.parseObject(a,String.class);
    }


    /**
     * 消息队列监听
     * @param message
     * @param channel
     */
    @RabbitListener(queues = RabbitConsts.FANOUT163)
    public void directHandlerManualAck163(Message message, Channel channel) {
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] str=message.getBody();
            Object a= JSONArray.parse(str);
            String object=doParse(a.toString());
            log.info("接收消息：{}", object);
            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                log.error("ACK失败！"+e.toString());
                // 处理失败,重新压入MQ
                channel.basicRecover();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
