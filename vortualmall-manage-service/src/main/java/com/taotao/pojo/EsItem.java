package com.taotao.pojo;

import java.util.List;

import javax.persistence.Column;

public class EsItem {
	private Long id;

	private String title;

	private String sellPoint;

	private Long price;

	private String image;

	private Integer status;
	
	private String category;
	
	private List<ItemSuggest> suggest;

	

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<ItemSuggest> getSuggest() {
		return suggest;
	}

	public void setSuggest(List<ItemSuggest> suggest) {
		this.suggest = suggest;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSellPoint() {
		return sellPoint;
	}

	public void setSellPoint(String sellPoint) {
		this.sellPoint = sellPoint;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
