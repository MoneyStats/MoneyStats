package com.moneystats.MoneyStats.commStats.category;

import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.timeTracker.LogTimeTracker;
import com.moneystats.timeTracker.LoggerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired private ICategoryDAO categoryDAO;

  /**
   * Used to get the list of categories avaiable for the wallet.
   *
   * @return a list of categories
   * @throws CategoryException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public List<CategoryEntity> categoryDTOList() throws CategoryException {
    List<CategoryEntity> categoryEntities = categoryDAO.findAll();
    if (categoryEntities.size() == 0) {
      LOG.error("Category Not Found, Not present into DB");
      throw new CategoryException(CategoryException.Code.CATEGORY_NOT_FOUND);
    }
    return categoryEntities;
  }
}
