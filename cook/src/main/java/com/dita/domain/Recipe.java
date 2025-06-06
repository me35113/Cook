package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "recipe")
@Getter
@Setter
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "recipe_id")
	private Integer recipeId;

	@Column(name = "user_id", length = 20)
	private String userId;

	@Column(name = "title", length = 100, nullable = false)
	private String title;

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "videos_url", columnDefinition = "TEXT")
	private String videosUrl;

	@Column(name = "servingCount")
	private Integer servingCount;

	@Column(name = "cooking_time", length = 20, nullable = false)
	private String cookingTime;

	@Column(name = "level", length = 20, nullable = false)
	private String level;

	@Column(name = "tip", columnDefinition = "TEXT")
	private String tip;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "views")
	private Integer views;

	@CreationTimestamp
	private Timestamp recDate;

	@Column(name = "completion_url", columnDefinition = "TEXT")
	private String completionUrl;

	@Column(name = "tags", columnDefinition = "TEXT")
	private String tags;

}
