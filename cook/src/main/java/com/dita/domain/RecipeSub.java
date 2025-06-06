package com.dita.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipe_sub")
@Getter
@Setter
public class RecipeSub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    @Column(name = "recipe_sub_id")
    private Integer recipesubId;
    
    @Column(name = "user_id", length = 20)
    private String userId;
    
    @Column(name = "recipe_id")
    private Integer recipeId;
    
    @Column(name = "state")
    private Integer state;
    
    @Column(name = "recipe_sub_date")
    private LocalDateTime recipesubDate;
    
    
}
