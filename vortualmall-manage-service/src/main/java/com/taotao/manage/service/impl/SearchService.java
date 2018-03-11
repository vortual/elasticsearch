package com.taotao.manage.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.VO.DataGridResult;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemCat;
import com.taotao.manage.service.ItemCatService;
import com.taotao.pojo.EsItem;
import com.taotao.pojo.ItemSuggest;

@Service
public class SearchService {
	@Autowired
	private TransportClient client;
	
	@Autowired
	private ItemCatService itemCatService;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public DataGridResult search(String keyWords, Integer page, Integer defaultRows) {
		return null;
	}

	public void addEsItemList(List<Item> items) throws JsonProcessingException {
		List<EsItem> list = new ArrayList<EsItem>();
		
		for (Item item : items) {
			Long cid = item.getCid();
			ItemCat itemCat = itemCatService.queryById(cid);
			
			EsItem esItem = new EsItem();
			esItem.setCategory(itemCat.getName());
			BeanUtils.copyProperties(item, esItem);
			updateSuggest(esItem);
			list.add(esItem);
		}
		
		for(int index = 0; index < list.size(); index++) {
			EsItem esItem = list.get(index);
			System.out.println("正在导第" + index + "个");
			client.prepareIndex("vortualmall", "item")
				.setSource(mapper.writeValueAsBytes(esItem),XContentType.JSON)
					.get();
		}
		
	}
	
	private void updateSuggest(EsItem esItem) {
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
                client, AnalyzeAction.INSTANCE, "vortualmall", esItem.getTitle());

        requestBuilder.setAnalyzer("ik_smart");

        AnalyzeResponse response = requestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();

        List<ItemSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排序数字类型 & 小于2个字符的分词结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }

            ItemSuggest suggest = new ItemSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }

        // 定制化自动补全
        ItemSuggest suggest = new ItemSuggest();
        if(StringUtils.isEmpty(esItem.getSellPoint())) {
        	esItem.setSellPoint("双十一大卖啦，走过路过不要错过");
        }
        suggest.setInput(esItem.getSellPoint());
        suggest.setWeight(88);
        suggests.add(suggest);
        
        ItemSuggest suggest2 = new ItemSuggest();
        suggest2.setInput(esItem.getCategory());
        suggest2.setWeight(50);
        suggests.add(suggest2);

        esItem.setSuggest(suggests);
    }
}
