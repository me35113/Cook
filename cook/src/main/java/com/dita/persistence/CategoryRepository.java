package com.dita.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dita.domain.Category;
import com.dita.domain.Recipe;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	
	@Query("SELECT r FROM Recipe r WHERE r.recipeId IN :ids ORDER BY r.recipeId DESC")
	List<Recipe> findByRecipeIdIn(@Param("ids") List<Integer> ids);
	
	@Query(value = """
		    SELECT DISTINCT recipe_id
		    FROM category
		    WHERE
		        (:hasType = true AND type IN (:types)) OR
		        (:hasFoodGroup = true AND food_group_id IN (:foodGroupIds)) OR
		        (:hasCountry = true AND country IN (:countries))
		    """, nativeQuery = true)
		List<Integer> findMatchingRecipeIds(
		    @Param("types") List<String> types,
		    @Param("foodGroupIds") List<Integer> foodGroupIds,
		    @Param("countries") List<String> countries,
		    @Param("hasType") boolean hasType,
		    @Param("hasFoodGroup") boolean hasFoodGroup,
		    @Param("hasCountry") boolean hasCountry
		);

	
}