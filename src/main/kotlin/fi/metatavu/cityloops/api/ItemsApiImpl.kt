package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.ItemsApi
import fi.metatavu.cityloops.api.spec.model.Item
import fi.metatavu.cityloops.api.translate.CategoryTranslator
import fi.metatavu.cityloops.api.translate.ItemTranslator
import fi.metatavu.cityloops.controllers.CategoryController
import fi.metatavu.cityloops.controllers.ItemController
import fi.metatavu.cityloops.controllers.ItemImageController
import fi.metatavu.cityloops.controllers.UserController

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
class ItemsApiImpl: ItemsApi, AbstractApi() {

  @Inject
  private lateinit var categoryController: CategoryController

  @Inject
  private lateinit var categoryTranslator: CategoryTranslator

  @Inject
  private lateinit var itemController: ItemController

  @Inject
  private lateinit var itemTranslator: ItemTranslator

  @Inject
  private lateinit var userController: UserController

  override fun createItem(payload: Item?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    payload ?: return createBadRequest("Missing request body")

    val title = payload.title
    val categoryId = payload.category ?: return createBadRequest("Missing category for item")
    val category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID $categoryId not found")
    val onlyForCompanies = payload.onlyForCompanies
    val userId = payload.userId
    val user = userController.findUserById(userId) ?: return createNotFound("User with ID: $userId could not be found")
    val metadata =  payload.metadata
    val images = payload.images
    val thumbnailUrl = payload.thumbnailUrl
    val itemProperties = payload.properties

    val item = itemController.createItem(
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      user = user,
      metadata = metadata,
      images = images,
      thumbnailUrl = thumbnailUrl,
      properties = itemProperties,
      creatorId = keycloakUserId
    )

    return createOk(itemTranslator.translate(item))
  }

  override fun listItems(userId: UUID?, firstResult: Int?, maxResults: Int?, sortByDateReturnOldestFirst: Boolean?): Response {

    var user: fi.metatavu.cityloops.persistence.model.User? = null
    if (userId != null) {
      user = userController.findUserById(userId) ?: return createNotFound("User with ID: $userId could not be found")
    }

    val items = itemController.listItems(firstResult, maxResults, sortByDateReturnOldestFirst, user)
    return createOk(items.map(itemTranslator::translate))
  }

  override fun findItem(itemId: UUID?): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    itemId ?: return createBadRequest("Missing item ID")

    val item = itemController.findItemById(itemId) ?: return createNotFound("Item with ID: $itemId could not be found!")
    return createOk(itemTranslator.translate(item))
  }

  override fun updateItem(itemId: UUID?, payload: Item?): Response {
    val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    payload ?: return createBadRequest("Missing request body")
    itemId ?: return createBadRequest("Missing item ID")

    val foundItem = itemController.findItemById(itemId) ?: return createNotFound("Item with ID: $itemId could not be found!")
    val title = payload.title
    val categoryId = payload.category ?: return createBadRequest("Missing category for item")
    val category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID $categoryId not found")
    val onlyForCompanies = payload.onlyForCompanies
    val metadata =  payload.metadata
    val images = payload.images
    val thumbnailUrl = payload.thumbnailUrl
    val itemProperties = payload.properties

    val item = itemController.updateItem(
      item = foundItem,
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      metadata = metadata,
      images = images,
      thumbnailUrl = thumbnailUrl,
      properties = itemProperties,
      lastModifierId = userId
    )

    return createOk(itemTranslator.translate(item))
  }

  override fun deleteItem(itemId: UUID?): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    itemId ?: return createBadRequest("Missing item ID")

    val item = itemController.findItemById(itemId) ?: return createNotFound("Item with ID: $itemId could not be found!")
    itemController.deleteItem(item)
    return createNoContent()
  }
}