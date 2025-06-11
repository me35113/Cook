package com.dita.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reply")
@Getter
@Setter
public class Reply {
	
	@Id
	@Column(name = "reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyId;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String reply;

    private String replyImage;

    @Column(name = "reply_create")
    private Timestamp replyCreate;

}
