package com.qf.listen;

import com.qf.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMQListener {
    @Autowired
    private Configuration configuration;

    @RabbitListener(queues = "goods_queue2")
    public void handleMsg(Goods goods){
        String gimage = goods.getGimage();
        String[] images = gimage.split("\\|");

        try {
            Template template = configuration.getTemplate("goodsitem.ftl");
            Map<String,Object> map = new HashMap<>();
            map.put("goods",goods);
            map.put("images",images);
            String path = this.getClass().getResource("/static/page/").getPath()+goods.getId()+".html";
            try {
                template.process(map,new FileWriter(path));
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
