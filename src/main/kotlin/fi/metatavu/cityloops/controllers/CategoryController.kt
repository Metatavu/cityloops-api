package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.api.spec.model.CategoryProperty
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
   * @param parentCategory parent category
   * @param properties category properties
   * @param creatorId creating user id
   * @return created category
   */
  fun createCategory(name: String, parentCategory: Category?, properties: List<CategoryProperty>, creatorId: UUID): Category {
    return categoryDAO.create(UUID.randomUUID(), name, parentCategory, getDataAsString(properties), creatorId, creatorId)
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
   * @param parentCategory parent category
   * @return list of categories
   */
  fun listCategories(parentCategory: Category?): List<Category> {
    return categoryDAO.list(parentCategory)
  }

  /**
   * Updates a category
   *
   * @param category category to be updated
   * @param name category name
   * @param parentCategory parent category
   * @param properties category properties
   * @param modifierId modifying user id
   * @return updated category
   */
  fun updateCategory(category: Category, name: String, parentCategory: Category?, properties: List<CategoryProperty>?, modifierId: UUID): Category {
    val result = categoryDAO.updateName(category, name, modifierId)
    categoryDAO.updateParentCategory(result, parentCategory, modifierId)
    categoryDAO.updateProperties(result, getDataAsString(properties), modifierId)
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

  /**
   * Serializes the object into JSON string
   *
   * @param data category properties
   * @return JSON string
   */
  private fun getDataAsString(data: List<CategoryProperty>?): String {
    val objectMapper = ObjectMapper()
    return objectMapper.writeValueAsString(data)
  }

}