package com.qf.shop_item;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopItemApplicationTests {
	@Autowired
	private Configuration configuration;
	@Test
	public void contextLoads() throws IOException, TemplateException {

		//准备一个静态页面输出的位置
		String outPath = "D:\\IDEAFiles\\shop_pom\\shop_web\\shop_item\\src\\main\\resources\\templates\\frametest.ftl";
		Writer writer = new FileWriter(outPath);
		//通过配置对象读取模板信息
		Template template = configuration.getTemplate("frametest.ftl");
		
		//利用map携带数据
		Map<String,Object> data = new HashMap<>();
		data.put("name","FreeMarker");
		//将模板和数据进行静态化合并（需要携带的数据，输出数据的地址）
		template.process(data,new FileWriter(outPath));

		writer.close();
	}

}
