package com.moneystats.MoneyStats.commStats.category;

import com.moneystats.MoneyStats.commStats.category.DTO.CategoryDTO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

  @Autowired private CategoryService categoryService;

  /**
   *
   * @return a list of categories
   * @throws CategoryException
   */
  @GetMapping("/list")
  public List<CategoryEntity> categoryGetList() throws CategoryException {
    return categoryService.categoryDTOList();
  }
}
