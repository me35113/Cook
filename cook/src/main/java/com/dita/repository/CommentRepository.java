package com.dita.repository;

import com.dita.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    int countByUserId(String userId);
    Page<Comment> findByUserId(String userId, Pageable pageable);
}
