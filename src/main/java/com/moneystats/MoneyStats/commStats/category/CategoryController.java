package com.moneystats.MoneyStats.commStats.category;

import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.generic.SchemaDescription;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/category")
@OpenAPIDefinition(tags = {@Tag(name = "Category", description = "")})
public class CategoryController {

  @Autowired private CategoryService categoryService;

  /**
   * @return a list of categories
   * @throws CategoryException
   */
  @GetMapping("/list")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_CATEGORY_SUMMARY,
      description = SchemaDescription.GET_CATEGORY_DESCRIPTION,
      tags = "Category")
  public List<CategoryEntity> categoryGetList() throws CategoryException {
    return categoryService.categoryDTOList();
  }
}
