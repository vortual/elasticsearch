package com.itheima.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.itheima.service.SearchService;
import com.taotao.common.VO.DataGridResult;

@Controller
@RequestMapping("search")
public class SearchController {
	@Autowired
	private SearchService service;
	// 默认页大小
	private static final Integer DEFAULT_ROWS = 20;

	@RequestMapping
	public ModelAndView search(@RequestParam(value = "q", required = false) String keyWords,
			@RequestParam(value = "page", defaultValue = "1")Integer page) {
		ModelAndView mv = new ModelAndView("search");
		if (StringUtils.isNotEmpty(keyWords)) {
			try {
				keyWords = new String(keyWords.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//搜索
			DataGridResult dataGridResult = service.search(keyWords, page, DEFAULT_ROWS);
			
			if(dataGridResult != null) {
				mv.addObject("itemList", dataGridResult.getRows());
				//计算总页数
				long totalPages = (dataGridResult.getTotal() + DEFAULT_ROWS - 1)/DEFAULT_ROWS;
				mv.addObject("totalPages", totalPages);
			}
			
			//设置search.jsp页面中需要的数据
			mv.addObject("query", keyWords);
			mv.addObject("page", page);
			
		}
		return mv;
	}
	
	@RequestMapping("/autocomplete")
	@ResponseBody
	public ApiResponse autoComplete(@RequestParam String prefix) throws Exception {
		if(StringUtils.isBlank(prefix)) {
			return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}
		//解决乱码问题
		prefix = new String(prefix.getBytes("ISO-8859-1"), "UTF-8");

		
		List<String> list = new ArrayList<String>();
		/*list.add("超棒瓦力");
		list.add("超棒vortual");*/
		
		List<String> suggest = service.suggest(prefix);
		
		return ApiResponse.ofSuccess(suggest);
	}
}
