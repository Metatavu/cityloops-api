package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.persistence.dao.CategoryDAO
import fi.metatavu.cityloops.persistence.model.Category
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for categories
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class CategoryController {

  @Inject
  private lateinit var categoryDAO: CategoryDAO

  /**
   * Creates new category
   *
   * @param name category name
   * @param creatorId creating user id
   * @return created category
   */
  fun createCategory(name: String, creatorId: UUID): Category {
    return categoryDAO.create(UUID.randomUUID(), name, creatorId, creatorId)
  }

  /**
   * Finds a category by id
   *
   * @param id category id
   * @return found category or null if not found
   */
  fun findCategoryById(id: UUID): Category? {
    return categoryDAO.findById(id)
  }

  /**
   * List of categories
   *
   * @return list of categories
   */
  fun listCategories(): List<Category> {
    return categoryDAO.listAll()
  }

  /**
   * Updates a category
   *
   * @param category category to be updated
   * @param name category name
   * @param modifierId modifying user id
   * @return updated category
   */
  fun updateCategory(category: Category, name: String, modifierId: UUID): Category {
    val result = categoryDAO.updateName(category, name, modifierId)
    return result
  }

  /**
   * Deletes a category
   *
   * @param category category to be deleted
   */
  fun deleteCategory(category: Category) {
    return categoryDAO.delete(category)
  }

}