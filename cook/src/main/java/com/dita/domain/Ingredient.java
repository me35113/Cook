package com.dita.domain;

import jakarta.persistence.*;

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

    // getters/setters
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public int getFoodGroupId() { return foodGroupId; }
    public void setFoodGroupId(int foodGroupId) { this.foodGroupId = foodGroupId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Integer getShelfLife() { return shelfLife; }
    public void setShelfLife(Integer shelfLife) { this.shelfLife = shelfLife; }
}
