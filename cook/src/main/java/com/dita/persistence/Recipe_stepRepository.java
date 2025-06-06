package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Recipe_step;

public interface Recipe_stepRepository extends JpaRepository<Recipe_step, Integer>{

}
