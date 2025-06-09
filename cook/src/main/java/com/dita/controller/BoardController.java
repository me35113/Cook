package com.dita.controller;

import com.dita.domain.Board;
import com.dita.domain.Report;
import com.dita.persistence.ReportRepository;
import com.dita.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private ReportRepository reportRepository;

    // 게시글 목록 조회
    @GetMapping("/boardlist")
    public String boardList(Model model) {
        List<Board> list = boardService.getAllBoards();
        model.addAttribute("list", list);
        return "boardlist";
    }

    // 글 작성 폼 이동
    @GetMapping("/boardlist_write")
    public String writeForm(Model model) {
        model.addAttribute("board", new Board());
        return "boardlist_write";
    }

    // 글 작성 처리
    @PostMapping("/boardlist_write")
    public String writeSubmit(@ModelAttribute Board board) {
        board.setUserId("1111"); // ← 고정된 member.user_id 값
        boardService.saveBoard(board);
        return "redirect:/boardlist";
    }

    // 글 상세 조회
    @GetMapping("/boardlist_read")
    public String readBoard(@RequestParam("board_id") Long boardId, Model model) {
        Board board = boardService.getBoard(boardId);
        model.addAttribute("dto", board);
        return "boardlist_read";
    }

    // 🚨 신고 처리
    @PostMapping("/report_proc")
    @ResponseBody
    public ResponseEntity<String> reportSubmit(@RequestParam("board_id") Long boardId,
                                               @RequestParam("type") String type,
                                               @RequestParam("title") String title,
                                               @RequestParam(value = "detail", required = false) String detail) {
        Report report = new Report();
        report.setRecipeId(boardId);  // board_id를 recipe_id 컬럼에 저장
        report.setType(type);
        report.setTitle(title);
        report.setDetail((detail != null && !detail.trim().isEmpty()) ? detail : null);
        reportRepository.save(report);
        return ResponseEntity.status(HttpStatus.FOUND)
                             .header("Location", "/boardlist")
                             .build();
    }
}
