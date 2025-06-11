package com.dita.controller;

import com.dita.domain.ReportC;
import com.dita.persistence.ReportCRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminCommentReportController {

    private final ReportCRepository reportCRepository;  // 변수명과 타입 일치

    public AdminCommentReportController(ReportCRepository reportCRepository) {
        this.reportCRepository = reportCRepository;
    }

    // 1) 목록 보기
    @GetMapping("/admincomment2")
    public String listCommentReports(Model model) {
        List<ReportC> reports = reportCRepository.findAll();
        model.addAttribute("reports", reports);
        return "admincomment2";  // templates/admincomment2.html
    }

    // 2) 선택 삭제
    @PostMapping("/admincomment2/delete")
    public String deleteCommentReports(@RequestParam("report_ids") List<Integer> reportIds) {
        if (!reportIds.isEmpty()) {
            reportCRepository.deleteAllById(reportIds);
        }
        return "redirect:/admincomment2";
    }
}
