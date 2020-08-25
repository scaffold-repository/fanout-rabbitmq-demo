package net.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class RabbitMqConfig {


    @Bean
    public RabbitAdmin rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message));
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new ContentTypeDelegatingMessageConverter(new Jackson2JsonMessageConverter());
    }


    /**
     * 直接模式队列1
     */
    @Bean
    public Queue directOneQueue155() {
        return new Queue(RabbitConsts.FANOUT155);
    }

    /**
     * 回响队列
     *
     * @return
     */
    @Bean
    public Queue directOneQueue163() {
        return new Queue(RabbitConsts.FANOUT163);
    }


    /**
     * 延迟队列交换器, x-delayed-type 和 x-delayed-message 固定
     */
    @Bean
    public FanoutExchange exchangeForJave() {
        return new FanoutExchange(RabbitConsts.FANOUTEXCHANGE);
    }

    /**
     * 延迟队列绑定自定义交换器
     * @param directOneQueue155 队列
     * @param exchangeForJave   延迟交换器
     */
    @Bean
    public Binding delayBinding(Queue directOneQueue155, FanoutExchange exchangeForJave) {
        return BindingBuilder.bind(directOneQueue155).to(exchangeForJave);
    }


    @Bean
    Binding bindingExchangeB(Queue directOneQueue163, FanoutExchange exchangeForJave) {
        return BindingBuilder.bind(directOneQueue163).to(exchangeForJave);
    }

}
