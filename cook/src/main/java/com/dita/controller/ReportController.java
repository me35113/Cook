package com.dita.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dita.domain.ReportC;
import com.dita.persistence.ReportCRepository;
import com.dita.persistence.ReportRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReportController {
	
	@Autowired
	private ReportRepository reportRepository;
	
	@Autowired
	private ReportCRepository reportCRepository;
	
	
	// ////////////// 댓글 신고 ///////////////////
	@PostMapping("/report_comment")
    public String reportComment(@RequestParam("comment_id") Integer commentId,
    		@RequestParam("recipe_id") Integer recipeId,
                                 @RequestParam(value = "reason", required = false) List<String> reasons,
                                 @RequestParam(value = "etc_reason", required = false) String etcReason,
                                 
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        if (reasons == null || reasons.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "신고 사유를 선택해주세요.");
            return "redirect:/"; // 실패 시 돌아갈 경로
        }

        String title = String.join(", ", reasons);
        if (etcReason != null && !etcReason.trim().isEmpty()) {
            title += ", 기타: " + etcReason;
        }

        ReportC report = new ReportC();
        report.setRecipeId(recipeId);
        report.setType("댓글 신고");
        report.setTitle(title);
        report.setDetail("기타 내용: " + etcReason);
        report.setCommentId(commentId);
        

        reportCRepository.save(report);

        redirectAttributes.addFlashAttribute("msg", "신고가 접수되었습니다.");
        
        return "redirect:/recipe_detail?recipe_id=" + recipeId;
    }
	
	

}
