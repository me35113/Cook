package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recipe_step")
public class Recipe_step {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "step_id")
    private Integer stepId;
    
    @Column(name = "recipe_id")
    private Integer recipeId;
    
    @Column(name = "image")
    private String image;
    
    @Column(name = "explanation")
    private String explanation;
    
}