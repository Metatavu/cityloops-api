package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.CategoriesApi
import fi.metatavu.cityloops.api.spec.model.Category
import fi.metatavu.cityloops.api.translate.CategoryTranslator
import fi.metatavu.cityloops.controllers.CategoryController
import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Categories API REST endpoints
 *
 * @author Jari Nykänen
 */
@RequestScoped
@Stateful
class CategoryApiImpl: CategoriesApi, AbstractApi() {

  @Inject
  private lateinit var categoryController: CategoryController

  @Inject
  private lateinit var categoryTranslator: CategoryTranslator

  override fun createCategory(payload: Category?): Response {
    val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    payload ?: return createBadRequest("Missing request body")

    val newName = payload.name

    var newParentCategory: fi.metatavu.cityloops.persistence.model.Category? = null
    val parentCategoryId = payload.parentCategoryId
    if (parentCategoryId != null) {
      newParentCategory = categoryController.findCategoryById(payload.parentCategoryId)
    }

    val createdCategory = categoryController.createCategory(
      name = newName,
      parentCategory = newParentCategory,
      creatorId = userId
    )

    return createOk(categoryTranslator.translate(createdCategory))
  }

  override fun listCategories(parentCategoryId: UUID?): Response? {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)

    var parentCategory: fi.metatavu.cityloops.persistence.model.Category? = null
    if (parentCategoryId != null) {
      parentCategory = categoryController.findCategoryById(parentCategoryId)
    }

    val categories = categoryController.listCategories(parentCategory)

    return createOk(categories.map(categoryTranslator::translate))
  }

  override fun deleteCategory(categoryId: UUID?): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    categoryId ?: return createBadRequest("Missing category ID")

    val categoryToDelete = categoryController.findCategoryById(id = categoryId) ?: return createNotFound("Could not find category with id: $categoryId")
    categoryController.deleteCategory(category = categoryToDelete)
    return createOk("")
  }

  override fun findCategory(categoryId: UUID?): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    categoryId ?: return createBadRequest("Missing category ID")

    val foundCategory = categoryController.findCategoryById(id = categoryId) ?: return createNotFound("Could not find category with id: $categoryId")
    return createOk(categoryTranslator.translate(foundCategory))
  }

  override fun updateCategory(categoryId: UUID?, payload: Category?): Response {
    val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    categoryId ?: return createBadRequest("Missing category ID")
    payload ?: return createBadRequest("Missing category payload")

    val foundCategory = categoryController.findCategoryById(id = categoryId) ?: return createNotFound("Could not find category with id: $categoryId")

    val newName = payload.name
    var newParentCategory: fi.metatavu.cityloops.persistence.model.Category? = null
    val parentCategoryId = payload.parentCategoryId
    if (parentCategoryId != null) {
      newParentCategory = categoryController.findCategoryById(payload.parentCategoryId)
    }

    val updatedCategory = categoryController.updateCategory(
      category = foundCategory,
      name = newName,
      parentCategory = newParentCategory,
      modifierId = userId
    )

    return createOk(categoryTranslator.translate(updatedCategory))
  }

}