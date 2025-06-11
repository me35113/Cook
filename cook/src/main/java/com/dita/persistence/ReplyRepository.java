package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dita.domain.Comment;
import com.dita.domain.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
	List<Reply> findByCommentOrderByReplyCreateAsc(Comment comment);

	List<Reply> findByComment(Comment comment);
	
	int countByComment(Comment comment);
}

