package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id") // 실제 DB 컬럼명에 맞게 매핑
    private Long boardId;

    private String title;

    @Column(length = 5000)
    private String content;

    @Column(name = "user_id") // DB 컬럼명이 user_id인 경우
    private String userId;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;
    
    @Column(name = "board_create")
    private LocalDateTime boardCreate;

    // 기본 생성자
    public Board() {
    }

    // 전체 필드 초기화 생성자
    public Board(Long boardId, String title, String content, String userId, int viewCount, LocalDateTime boardCreate) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.viewCount = viewCount;
        this.boardCreate = boardCreate;
    }
}
