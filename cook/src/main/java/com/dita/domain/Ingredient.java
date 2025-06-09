package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "food_group_id")
    private int foodGroupId;

    @Column(name = "method")
    private String method;

    @Column(name = "shelf_life")
    private Integer shelfLife;

   
}
