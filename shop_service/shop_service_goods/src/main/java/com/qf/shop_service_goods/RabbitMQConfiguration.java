package com.qf.shop_service_goods;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfiguration {
    //定义为常量方便使用
    public static final String FANOUT_NAME = "goods_fanoutExchange";

    /**
     * 创建两个rabbitMQ消息队列
     * @return
     */
    @Bean
    public Queue getQueue1(){//Queue为spring包下的
        return new Queue("goods_queue1");
    }
    @Bean
    public Queue getQueue2(){//Queue为spring包下的
        return new Queue("goods_queue2");
    }

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public FanoutExchange getFanoutExchange(){
        return new FanoutExchange(FANOUT_NAME);
    }

    /**
     * 绑定交换机，将队列与交换机方法名作为参数写入会自动绑定
     * @param getQueue1
     * @param getFanoutExchange
     * @return
     */
    @Bean
    public Binding getBindingBuilder1(Queue getQueue1, FanoutExchange getFanoutExchange){
        return BindingBuilder.bind(getQueue1).to(getFanoutExchange);
    }
    @Bean
    public Binding getBindingBuilder2(Queue getQueue2, FanoutExchange getFanoutExchange){
        return BindingBuilder.bind(getQueue2).to(getFanoutExchange);
    }
}
