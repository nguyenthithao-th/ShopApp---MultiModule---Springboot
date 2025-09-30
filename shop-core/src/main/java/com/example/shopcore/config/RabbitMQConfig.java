package com.example.shopcore.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    //shop-exchange
    public static final String SHOP_EXCHANGE = "shop.exchange";

    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_VERIFIED_QUEUE = "order.verified.queue";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_VERIFIED_ROUTING_KEY = "order.verified";

    // Payment queues
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.queue";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    public static final String ORDER_CREATED_PAYMENT_QUEUE = "order.created.payment.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue orderVerifiedQueue() {
        return new Queue(ORDER_VERIFIED_QUEUE, true);
    }


    @Bean
    public Binding bindingCreated(
            @Qualifier("orderCreatedQueue") Queue orderCreatedQueue,
            @Qualifier("orderExchange") DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(orderExchange).with("order.created");
    }

    @Bean
    public Binding bindingVerified(
            @Qualifier("orderVerifiedQueue") Queue orderVerifiedQueue,
            @Qualifier("orderExchange") DirectExchange orderExchange) {
        return BindingBuilder.bind(orderVerifiedQueue)
                .to(orderExchange).with("order.verified");
    }


    @Bean
    public DirectExchange shopExchange() {
        return new DirectExchange(SHOP_EXCHANGE);
    }

    // ---- Payment ----
    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue(PAYMENT_COMPLETED_QUEUE, true);
    }

    @Bean
    public Binding bindingPaymentCompleted() {
        return BindingBuilder.bind(paymentCompletedQueue())
                .to(shopExchange()).with(PAYMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Queue orderCreatedPaymentQueue() {
        return new Queue("order.created.payment.queue", true);
    }

    @Bean
    public Binding bindingOrderCreatedPayment(
            @Qualifier("orderCreatedPaymentQueue")Queue orderCreatedPaymentQueue,
            @Qualifier("orderExchange")DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedPaymentQueue)
                .to(orderExchange)
                .with("order.created");
    }


}
