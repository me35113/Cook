package com.dita.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dita.domain.MemberSub;
import com.dita.persistence.MemberSubRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberSubController {

	@Autowired
	private MemberSubRepository memberSubRepository;
	

	@PostMapping("/subscribe-toggle")
	public String toggleSubscription(
	        @RequestParam("subed_user") String subedUser,
	        @RequestParam("recipe_id") int recipeId,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) 
	{
	    // 세션에서 idKey로 로그인 ID 가져오기 (댓글과 동일하게)
	    String loginId = (String) session.getAttribute("idKey");

	    // ////////확인용 코드 ////////////
	    System.out.println("✅ [구독 요청 도착]");
	    System.out.println("🔸 loginId (from idKey): " + loginId);
	    System.out.println("🔸 subedUser: " + subedUser);
	    System.out.println("🔸 recipeId: " + recipeId);

	    if (loginId == null) {
	        System.out.println("로그인 정보 없음. 로그인 페이지로 이동");
	        return "redirect:/login";
	    }
	    

	 // /////// 기존 구독 여부 확인 //////////
	    MemberSub sub = memberSubRepository.findBySubUserAndSubedUser(loginId, subedUser);

	    if (sub != null) {
	        // 구독 상태 토글
	        int newState = (sub.getState() != null && sub.getState() == 1) ? 0 : 1;
	        sub.setState(newState);
	        sub.setMemSubDate(LocalDateTime.now()); // 날짜 업데이트
	        memberSubRepository.save(sub);
	        redirectAttributes.addAttribute("msg", newState == 1 ? "subscribed" : "unsubscribed");
	    } else {
	        // 처음 구독할 경우: INSERT
	        MemberSub newSub = new MemberSub();
	        newSub.setSubUser(loginId);
	        newSub.setSubedUser(subedUser);
	        newSub.setState(1);
	        newSub.setMemSubDate(LocalDateTime.now()); 
	        memberSubRepository.save(newSub);
	        redirectAttributes.addAttribute("msg", "subscribed");
	    }
	    
	    return "redirect:/recipe_detail?recipe_id=" + recipeId;
	}
}
