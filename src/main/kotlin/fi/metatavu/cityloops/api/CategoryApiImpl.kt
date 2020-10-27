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
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    payload ?: return createBadRequest("Missing request body")

    val newName = payload.name
    val properties = payload.properties
    var newParentCategory: fi.metatavu.cityloops.persistence.model.Category? = null
    val parentCategoryId = payload.parentCategoryId
    if (parentCategoryId != null) {
      newParentCategory = categoryController.findCategoryById(payload.parentCategoryId)
    }

    val createdCategory = categoryController.createCategory(
      name = newName,
      parentCategory = newParentCategory,
      properties = properties,
      creatorId = keycloakUserId
    )

    return createOk(categoryTranslator.translate(createdCategory))
  }

  override fun listCategories(parentCategoryId: UUID?): Response? {

    if (!isAnonymous && !isUser) {
      return createUnauthorized(FORBIDDEN)
    }

      var parentCategory: fi.metatavu.cityloops.persistence.model.Category? = null
    if (parentCategoryId != null) {
      parentCategory = categoryController.findCategoryById(parentCategoryId)
    }

    val categories = categoryController.listCategories(parentCategory)

    return createOk(categories.map(categoryTranslator::translate))
  }

  override fun findCategory(categoryId: UUID?): Response {
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }
    categoryId ?: return createBadRequest("Missing category ID")

    val foundCategory = categoryController.findCategoryById(id = categoryId) ?: return createNotFound("Could not find category with id: $categoryId")
    return createOk(categoryTranslator.translate(foundCategory))
  }

  override fun updateCategory(categoryId: UUID?, payload: Category?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    categoryId ?: return createBadRequest("Missing category ID")
    payload ?: return createBadRequest("Missing category payload")

    val foundCategory = categoryController.findCategoryById(id = categoryId) ?: return createNotFound("Could not find category with id: $categoryId")

    val newName = payload.name
    val properties = payload.properties
    var newParentCategory: fi.metatavu.cityloops.persistence.model.Category? = null
    val parentCategoryId = payload.parentCategoryId
    if (parentCategoryId != null) {
      newParentCategory = categoryController.findCategoryById(payload.parentCategoryId)
    }

    val updatedCategory = categoryController.updateCategory(
      category = foundCategory,
      name = newName,
      parentCategory = newParentCategory,
      properties = properties,
      modifierId = keycloakUserId
    )

    return createOk(categoryTranslator.translate(updatedCategory))
  }

  override fun deleteCategory(categoryId: UUID?): Response {
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    categoryId ?: return createBadRequest("Missing category ID")

    val category = categoryController.findCategoryById(id = categoryId) ?: return createNotFound("Could not find category with id: $categoryId")
    categoryController.deleteCategory(category)
    return createNoContent()
  }

  companion object {
    private const val NOT_FOUND_MESSAGE = "Not found"
    private const val UNAUTHORIZED = "Unauthorized"
    private const val FORBIDDEN = "Forbidden"
  }

}