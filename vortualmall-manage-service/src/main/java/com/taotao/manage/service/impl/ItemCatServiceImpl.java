package com.taotao.manage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taotao.manage.mapper.ItemCatMapper;
import com.taotao.manage.pojo.ItemCat;
import com.taotao.manage.service.ItemCatService;

@Service
@Transactional
public class ItemCatServiceImpl extends BaseServiceImpl<ItemCat> implements ItemCatService {
	@Autowired
	private ItemCatMapper itemCatMapper;

}
