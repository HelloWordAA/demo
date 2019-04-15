package com.qf.listen;

import com.qf.entity.Goods;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {
    @Autowired
    private SolrClient solrClient;


    /**
     * //监听goods_queue1队列的消息
     * 监听到商品添加后，将商品信息同步至索引库中
     * @param goods
     */
    @RabbitListener(queues = "goods_queue1")
    public void handMsg(Goods goods){
        System.out.println("MQ的消息"+goods);
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id",goods.getId());
        document.setField("gname",goods.getGname());
        document.setField("gimage",goods.getGimage());
        document.setField("ginfo",goods.getGinfo());
        document.setField("gsave",goods.getGsave());
        document.setField("gprice",goods.getGprice().doubleValue());
        try {
            solrClient.add(document);
            solrClient.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
