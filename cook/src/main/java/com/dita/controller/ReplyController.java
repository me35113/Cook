package com.dita.controller;

import com.dita.domain.Comment;
import com.dita.domain.Member;
import com.dita.domain.Reply;
import com.dita.persistence.CommentRepository;
import com.dita.persistence.MemberRepository;
import com.dita.persistence.ReplyRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ReplyController {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/reply/write")
    public String writeReply(
            Integer recipeId,
            Long commentId,
            String reply,
            MultipartFile replyImage,
            HttpSession session,
            Model model) {

        // 세션에서 사용자 ID 가져오기
        String userId = (String) session.getAttribute("idKey");
        if (userId == null) {
            return "redirect:/login"; // 로그인하지 않은 사용자 처리
        }

        // 댓글 확인
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            model.addAttribute("error", "댓글을 찾을 수 없습니다.");
            return "redirect:/recipe_detail?recipe_id=" + recipeId;
        }

        Reply newReply = new Reply();
        newReply.setComment(optionalComment.get());
        newReply.setUserId(userId);
        newReply.setReplyCreate(new Timestamp(System.currentTimeMillis()));
        newReply.setReply(reply);

        // 이미지 처리
        if (replyImage != null && !replyImage.isEmpty()) {
            try {
                String originalFilename = replyImage.getOriginalFilename();
                String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                String uploadPath = "C:/Jsp/myapp/src/main/webapp/upload/";
                File file = new File(uploadPath + newFilename);
                replyImage.transferTo(file);
                newReply.setReplyImage(newFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        replyRepository.save(newReply);
        return "redirect:/recipe_detail?recipe_id=" + recipeId;
    }
}
