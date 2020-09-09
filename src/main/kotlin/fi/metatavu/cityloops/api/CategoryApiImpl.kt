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

    val createdCategory = categoryController.createCategory(
      name = payload.name,
      creatorId = userId
    )

    return createOk(categoryTranslator.translate(createdCategory))
  }

  override fun listCategories(): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    val categories = categoryController.listCategories()

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

    val name = payload.name
    val updatedCategory = categoryController.updateCategory(
      category = foundCategory,
      name = name,
      modifierId = userId
    )

    return createOk(categoryTranslator.translate(updatedCategory))
  }

}