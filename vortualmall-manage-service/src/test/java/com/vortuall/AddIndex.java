package com.vortuall;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taotao.manage.pojo.Item;
import com.taotao.manage.service.ItemService;
import com.taotao.manage.service.impl.ItemServiceImpl;
import com.taotao.manage.service.impl.SearchService;

public class AddIndex {
	private SearchService searchService;
	private ItemService itemService;

	@Before
	public void setUp() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
		searchService = context.getBean(SearchService.class);
		itemService = context.getBean(ItemServiceImpl.class);
	}

	@Test
	public void addIndex() throws Exception {
		Integer page = 1;
		Integer pageSize = 500;
		List<Item> itemList = null;
		do {
			System.out.println("----------正在导出第" + page + "页..。");
			// 1、获取商品列表
			itemList = itemService.queryListByPage(page, pageSize);
			// 3、添加到solr中
			searchService.addEsItemList(itemList);

			System.out.println("----------导入第" + page + "页完成。");

			page++;
			pageSize = itemList.size();// 是否继续遍历的依据
		} while (pageSize == 500);

	}

}
