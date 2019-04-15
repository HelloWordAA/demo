package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import com.qf.service.ISearchService;
import com.qf.shop_service_goods.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class GoodsService implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Reference
    private ISearchService searchService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Goods> queryAllGoods() {
        return goodsMapper.selectList(null);
    }

    @Override
    public int addGoods(Goods goods) {
        int result = goodsMapper.insert(goods);
        //需要主键，mybatis本身带有主键回填，只需要到entity中添加主键注解
//        searchService.addGoods(goods);
        //将添加商品的信息放入消息对列中，（交换机名字，路由键【无】，需要传的值）goods需要实现序列化
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.FANOUT_NAME,"",goods);
        return result;
    }

    @Override
    public Goods queryById(int id) {
        return goodsMapper.selectById(id);
    }
}
