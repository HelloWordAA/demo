package com.qf.shop_search;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopSearchApplicationTests {
	@Autowired
	private SolrClient solrClient;


	@Test
	public void add() {
		SolrInputDocument solrDocument = new SolrInputDocument();
		solrDocument.addField("id",1);
		solrDocument.addField("gname","冥域青龙");
		solrDocument.addField("gimage","http://www.baidu.com");
		solrDocument.addField("ginfo","威力猛");
		solrDocument.addField("gprice",10000.00);
		solrDocument.addField("gsave",100);
		try {
			solrClient.add(solrDocument);
			solrClient.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void update() {
		SolrInputDocument solrDocument = new SolrInputDocument();
		solrDocument.addField("id", 3);
		solrDocument.addField("gname", "华为手机");
		solrDocument.addField("gimage", "http://www.baidu.com");
		solrDocument.addField("ginfo", "手机中的战斗机");
		solrDocument.addField("gprice", 99.99);
		solrDocument.addField("gsave", 10000);
		try {
			solrClient.add(solrDocument);
			solrClient.commit();
		} catch (Exception e) {
			e.printStackTrace();

	}
}
}
