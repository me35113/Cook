package com.dita.domain;

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


@Entity
@Table(name = "recipe_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	
    private Integer id;

    @Column(name = "step_id")
    private Integer stepId;

    @Column(name = "recipe_id")
    private Integer recipeId;

    private String image;

    private String explanation;
}
