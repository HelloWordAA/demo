package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.entity.Goods;
import com.qf.service.ISearchService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService implements ISearchService {
    @Autowired
    private SolrClient solrClient;
    @Override
    public List<Goods> searchByKeyWord(String keyWord) {
        System.out.println("搜索服务"+keyWord);
        SolrQuery solrQuery = new SolrQuery();
        if(keyWord==null || keyWord.equals("")){//如果keyword为空搜索全部
            solrQuery.setQuery("*:*");  //后面不是*前面也不能是*
        }else{
            //设置搜索关键字为gname，或ginfo里面的
            solrQuery.setQuery("gname:"+keyWord+" || ginfo:"+keyWord);
        }
        /*设置高亮*/
        solrQuery.setHighlight(true);
        /*设置高亮的颜色样式*/
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        //需要添加高亮的字段
        solrQuery.addHighlightField("gname");

            List<Goods> goodlist = new ArrayList<>();

        try {
            //solr客户端返回查找结果
            QueryResponse result = solrClient.query(solrQuery);
            //<有高亮的id,当前商品有高亮的字段，高亮的内容>
            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();
            //获得结果集
            SolrDocumentList results = result.getResults();
            /*SolrDocumentList不方便使用，转为ArrayList*/
            for(SolrDocument document:results){ //将SolrDocumentList中的结果遍历出来，每个document相当于一件商品
                Goods goods = new Goods();
                //document.get()返回的是Object类型，需要加“”变为String，再转为需要的类型
                goods.setId(Integer.parseInt(document.get("id")+""));
                goods.setGname(document.get("gname")+"");
                goods.setGprice(BigDecimal.valueOf(Double.parseDouble(document.get("gprice")+"")));
                goods.setGimage(document.get("gimage")+"");
//                goods.setGinfo(document.get("ginfo")+""); //商品信息不用显示
                goods.setGsave((Integer.parseInt(document.get("gsave")+"")));

                /*判断商品有无高亮*/
                if(highlighting.containsKey(goods.getId()+"")){//判断集合中是否有该商品的id
                    //从高亮集合中查找出该商品的信息集合
                    Map<String, List<String>> stringListMap = highlighting.get(goods.getId() + "");
                    //获取商品的高亮，取第一个
                    String gname = stringListMap.get("gname").get(0);
                    //将商品的名字替换为高亮名字
                    goods.setGname(gname);
                }

                goodlist.add(goods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goodlist;
    }

    @Override
    public int addGoods(Goods goods) {
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
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
