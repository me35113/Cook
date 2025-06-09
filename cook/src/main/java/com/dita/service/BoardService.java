package com.dita.service;

import com.dita.domain.Board;
import com.dita.persistence.BoardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 전체 게시글 조회
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 게시글 1개 조회 (조회수 증가 포함)
    public Board getBoard(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setViewCount(board.getViewCount() + 1);
            boardRepository.save(board);
        }
        return board;
    }

    // 게시글 저장
    public void saveBoard(Board board) {
        board.setBoardCreate(LocalDateTime.now()); // 작성 시간 저장
        board.setViewCount(0); // 조회수 초기화
        boardRepository.save(board);
    }
}
