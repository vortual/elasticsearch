package com.itheima.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.itheima.pojo.EsItem;
import com.taotao.common.VO.DataGridResult;

@Service
public class SearchService {
	@Autowired
	private TransportClient client;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static Logger logger = LoggerFactory.getLogger(SearchService.class);

	public DataGridResult search(String keyWords, Integer page, Integer defaultRows) {
		int from = (page-1) * defaultRows;
		
		DataGridResult result = new DataGridResult();
		
		List<EsItem> list = new ArrayList<>();
		
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		boolQueryBuilder.filter(QueryBuilders.termQuery("title", keyWords));
		boolQueryBuilder.must(QueryBuilders.matchQuery("status", 1));
		
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("title");
		
		SearchRequestBuilder requestBuilder = client.prepareSearch("vortualmall").setTypes("item")
				.setQuery(boolQueryBuilder).setFrom(from).setSize(defaultRows).highlighter(highlightBuilder);
		
		logger.error(requestBuilder.toString());
		
		SearchResponse searchResponse = requestBuilder.get();
		
		SearchHits hits = searchResponse.getHits();
		
		result.setTotal(hits.getTotalHits());
		
		for (SearchHit searchHit : hits) {
			//获取高亮
			Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
			HighlightField highlightField = highlightFields.get("title");
			Text text = highlightField.getFragments()[0];
			String title = text.string();
			System.out.println(title);
			
			Map<String, Object> source = searchHit.getSource();
			EsItem item = new EsItem();
			Long id =  Long.valueOf(String.valueOf(source.get("id")));
			String sellPoint = String.valueOf(source.get("sellPoint"));
			
			Long price = Long.valueOf(String.valueOf(source.get("price")));
			
			String image = String.valueOf(source.get("image"));
			int status = (int) source.get("status");
			
			item.setId(id);
			item.setImage(image);
			item.setPrice(price);
			item.setSellPoint(sellPoint);
			item.setStatus(status);
			item.setTitle(title);
			
			list.add(item);
		}
		
		result.setRows(list);
		
		return result;
	}

	public List<String> suggest(String prefix) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest")
        		.prefix(prefix)
        			.size(8);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("autocomplete", suggestion);

        SearchRequestBuilder requestBuilder = this.client.prepareSearch("vortualmall")
                .setTypes("item")
                	.suggest(suggestBuilder)
                		.setFetchSource("suggest", null);
        logger.error(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        Suggest suggest = response.getSuggest();
        if (suggest == null) {
            return new ArrayList<>();
        }
        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

        int maxSuggest = 0;
        Set<String> suggestSet = new HashSet<>();

        for (Object term : result.getEntries()) {
            if (term instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;

                if (item.getOptions().isEmpty()) {
                    continue;
                }

                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    System.out.println(tip);
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }
            }

            if (maxSuggest > 8) {
                break;
            }
        }
        List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
        return suggests;
    }
}
