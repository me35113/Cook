package com.dita.domain;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private int storeId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "ingredient_name")
    private String ingredientName;

    private String quantity;

    @Column(name = "store_create")
    private String storeCreate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    // Getters and Setters
    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getStoreCreate() { return storeCreate; }
    public void setStoreCreate(String storeCreate) { this.storeCreate = storeCreate; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }
}
