package com.moneystats.MoneyStats.commStats.category;

import com.moneystats.MoneyStats.commStats.category.DTO.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

  @Autowired private CategoryService categoryService;

  @GetMapping("/list")
  public List<CategoryDTO> categoryGetList() throws CategoryException {
    return categoryService.categoryDTOList();
  }
}
