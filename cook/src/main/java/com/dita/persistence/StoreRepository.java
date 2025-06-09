package com.dita.persistence;

import com.dita.domain.Store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Integer> {
	// store와 ingredient를 내부 조인해서 ingredient가 존재하는 store만 조회 (JPQL 예시)
	@Query("SELECT s FROM Store s JOIN Ingredient i ON s.ingredientName = i.ingredientName WHERE s.userId = :userId")
	Page<Store> findByUserIdWithIngredient(@Param("userId") String userId, Pageable pageable);
	@Query("SELECT s FROM Store s WHERE s.userId = :userId")
	Page<Store> findByUserIdWithIngredient2(@Param("userId") String userId, Pageable pageable);

    // 필요하면 전체 조회도 유지 가능
    List<Store> findByUserId(String userId);
    
    @Query("SELECT s.ingredientName FROM Store s WHERE s.userId = :userId")
    List<String> findIngredientNamesByUserId(@Param("userId") String userId);
}
