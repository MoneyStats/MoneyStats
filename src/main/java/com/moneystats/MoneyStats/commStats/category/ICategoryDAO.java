package com.moneystats.MoneyStats.commStats.category;

import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryDAO extends JpaRepository<CategoryEntity, Integer> {}
