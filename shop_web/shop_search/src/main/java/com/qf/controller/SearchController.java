package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.service.ISearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Reference
    private ISearchService searchService;
    @RequestMapping("/searchByKeyWord")
    public String searchByKeyWord(String keyWord,Model model) {
        System.out.print("搜索工程"+keyWord);
        List<Goods> goods = searchService.searchByKeyWord(keyWord);
        System.out.println("搜索结果"+goods);
        model.addAttribute("goods",goods);
        return "searchlist";
    }
}
