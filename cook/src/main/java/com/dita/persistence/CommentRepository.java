package com.dita.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	// 특정 레시피의 댓글 목록 (최신순)
    List<Comment> findByRecipeIdOrderByCommentCreateDesc(Integer recipeId);
    
    int countByUserId(String userId);
    Page<Comment> findByUserId(String userId, Pageable pageable);


}
