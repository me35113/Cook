package com.dita.vo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;

//VO : 불변성격에 값을 저장하는 Model
@Getter
public class PageVO {

	private static final int DEFAULT_SIZE = 12;
	private static final int DEFAULT_MAX_SIZE = 50;
	
	private int page;
	private int size;
	private String type;
	private String keyword;
	
	public PageVO() {
		this.page = 1;
		this.size = DEFAULT_SIZE;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public void setPage(int page) {
		this.page = page <= 0? 1 : page;
	}
	
	public void setSize(int size) {
		this.size = size <DEFAULT_SIZE||
				size > DEFAULT_MAX_SIZE? DEFAULT_SIZE : size;
	}
	
	//페이징 처리와 정렬을 설정
	//String...props : ("bno"), ("bno", "title")
	public Pageable makePageable(int direction, String...props) {
		Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
		return PageRequest.of(this.page-1, this.size, dir, props);
	}
}


