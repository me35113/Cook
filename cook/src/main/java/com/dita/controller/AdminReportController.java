package com.dita.controller;

import com.dita.domain.Report2;
import com.dita.persistence.Report2Repository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminReportController {

    private final Report2Repository reportRepo;

    public AdminReportController(Report2Repository reportRepo) {
        this.reportRepo = reportRepo;
    }

    @GetMapping("/adminboard2")
    public String listReports(Model model) {
        List<Report2> reports = reportRepo.findAll();
        model.addAttribute("reports", reports);
        return "adminboard2";  // → /templates/admin/reports.html (Thymeleaf) 또는 /WEB-INF/views/admin/reports.jsp
}
        @PostMapping("/adminboard2/delete")
        public String deleteReports(@RequestParam("board_ids") List<Long> boardIds) {
            if (!boardIds.isEmpty()) {
                // deleteAllById: 내부적으로 각각 find + delete 호출
                reportRepo.deleteAllById(boardIds);
                // 더 빠른 배치 삭제를 원하면 JpaRepository.deleteAllByIdInBatch(boardIds) 사용 가능
            }
            // 삭제 후 다시 목록 페이지로 리다이렉트
            return "redirect:/adminboard2";
    
    }
}
