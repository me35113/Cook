package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dita.domain.MemberSub;
import com.dita.persistence.MemberSubRepository;

@Controller
public class MemberSubController {

	@Autowired
	private MemberSubRepository memsubRepo;
	
	@PostMapping("/subscribe")
    public String handleSubscription(
            @RequestParam("sub_user") String subUser,
            @RequestParam("subed_user") String subedUser,
            @RequestParam("recipe_id") int recipeId,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes) {

        if ("구독".equals(action)) {
            // 이미 존재하지 않을 경우에만 추가
            if (!memsubRepo.existsBySubUserAndSubedUser(subUser, subedUser)) {
                MemberSub sub = new MemberSub();
                sub.setSubUser(subUser);
                sub.setSubedUser(subedUser);
                memsubRepo.save(sub);
            }
            redirectAttributes.addAttribute("msg", "subscribed");
        } else if ("구독취소".equals(action)) {
            memsubRepo.deleteBySubUserAndSubedUser(subUser, subedUser);
            redirectAttributes.addAttribute("msg", "unsubscribed");
        }

        return "redirect:/recipe_detail?recipe_id=" + recipeId;
    }
}
