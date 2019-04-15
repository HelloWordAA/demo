package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private IGoodsService goodsService;
    @Value("${server.ip}")
    private String serverIp;
    @RequestMapping("/list")
    public String queryAllGoods(Model model){
        List<Goods> goodsList = goodsService.queryAllGoods();
        model.addAttribute("goodlist",goodsList);
        model.addAttribute("serverip",serverIp);
        return "goodlist";
    }
    @RequestMapping("/addGoods")
    public String addGoods(Goods goods){
        goodsService.addGoods(goods);
        return "redirect:/goods/list";
    }
}
