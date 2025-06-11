package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.dita.domain.RecipeSub;
import com.dita.persistence.RecipeSubRepository;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam Integer recipeId,
            @RequestParam Integer currentState,
            HttpSession session) {

        String userId = (String) session.getAttribute("idKey");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        int newState;
        if (currentState == 0) {
            // 좋아요 추가
            String sql = "INSERT INTO recipe_sub (user_id, recipe_id, state, recipe_sub_date) " +
                         "VALUES (?, ?, ?, NOW()) " +
                         "ON DUPLICATE KEY UPDATE state = ?, recipe_sub_date = NOW()";
            jdbcTemplate.update(sql, userId, recipeId, 1, 1);
            newState = 1;
        } else {
            // 좋아요 취소
            String sql = "UPDATE recipe_sub SET state = 0, recipe_sub_date = NOW() " +
                         "WHERE user_id = ? AND recipe_id = ?";
            jdbcTemplate.update(sql, userId, recipeId);
            newState = 0;
        }

        // 좋아요 수 계산
        String countSql = "SELECT COUNT(*) FROM recipe_sub WHERE recipe_id = ? AND state = 1";
        int likeCount = jdbcTemplate.queryForObject(countSql, Integer.class, recipeId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "state", newState,
            "count", likeCount
        ));
    }
}