package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Reference
    private IGoodsService goodsService;
    @Autowired
    private Configuration configuration;

    @RequestMapping("/creatHtml")
    public String creatHtml(int id, HttpServletRequest request){    //商品id，url地址，因为静态页面已经脱离了范围获取不到，只能后台传过去
            //通过id调用商品服务查询商品详细信息
            Goods goods = goodsService.queryById(id);
            /*ftl页面没有分割处理，所以后台先分好再传到前台*/
            String gimage = goods.getGimage();
        String[] images = gimage.split("|");
        try {
            //获得商品详情的模板对象
            Template template = configuration.getTemplate("goodsitem.ftl");
            /*用来传数据*/
            Map<String,Object> map = new HashMap<>();
            map.put("goods", goods);//商品信息
            map.put("context",request.getContextPath()); //访问路径
            map.put("images",images);
            //获得ClassPath路径，如果文件为空则idea不会编译，包里加个空文件即可编译
            String path = this.getClass().getResource("/static/page/").getPath()+goods.getId()+".html";
            try {
                template.process(map,new FileWriter(path));
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
