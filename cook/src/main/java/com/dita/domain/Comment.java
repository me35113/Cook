package com.dita.domain;


import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Entity
@ToString
@Table(name = "comment")
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "comment_id")
	private Integer commentId;
	
	@Column(name = "user_id", length = 20)
	private String userId;
	
	@Column(name = "recipe_id")
	private Integer recipeId;
	
	@Column(name = "comment")
    private String comment;
    
    @Column(name = "comment_create")
    private Timestamp commentCreate;
	
    private String commentImage;
	

	
}
