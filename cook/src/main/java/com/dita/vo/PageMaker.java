package com.dita.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

@Getter
@ToString(exclude = "pageList")
@Log
public class PageMaker<T> {

    private Page<T> result;

    private Pageable prevPage; // 이전 페이지 정보
    private Pageable nextPage; // 다음 페이지 정보

    private int currentPageNum; // 화면에 보이는 1부터 시작하는 페이지 번호
    private int totalPageNum;   // 전체 페이지 개수

    private Pageable currentPage; // 현재 페이지 정보

    private List<Pageable> pageList; // 페이지 번호 시작부터 끝까지 Pageable 정보 리스트

    public PageMaker(Page<T> result) {
        this.result = result;
        this.currentPage = result.getPageable();
        this.currentPageNum = currentPage.getPageNumber() + 1;
        this.totalPageNum = result.getTotalPages();
        this.pageList = new ArrayList<>();
        calcPages();
    }

    private void calcPages() {
        int current = this.currentPageNum;
        int total = this.totalPageNum;

        int startNum = Math.max(current - 4, 1);
        int endNum = Math.min(current + 5, total);

        for (int i = startNum; i <= endNum; i++) {
            Pageable page = PageRequest.of(i - 1, currentPage.getPageSize());
            pageList.add(page);
        }

        if (result.hasPrevious()) {
            this.prevPage = pageList.get(0).previousOrFirst();
        }

        if (result.hasNext()) {
            this.nextPage = pageList.get(pageList.size() - 1).next();
        }
        
        if (currentPageNum < totalPageNum) {
            this.nextPage = PageRequest.of(currentPageNum, currentPage.getPageSize());
        }
    }
}
