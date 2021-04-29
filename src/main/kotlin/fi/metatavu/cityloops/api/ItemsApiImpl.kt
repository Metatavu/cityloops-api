package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.ItemsApi
import fi.metatavu.cityloops.api.spec.model.Item
import fi.metatavu.cityloops.api.spec.model.ItemType
import fi.metatavu.cityloops.api.translate.CategoryTranslator
import fi.metatavu.cityloops.api.translate.ItemTranslator
import fi.metatavu.cityloops.controllers.*
import java.time.OffsetDateTime

import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Items API REST endpoints
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

  @Inject
  private lateinit var searchHoundController: SearchHoundController

  override fun createItem(payload: Item?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }
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
    val price = payload.price
    val priceUnit = payload.priceUnit
    val paymentMethod = payload.paymentMethod
    val delivery = payload.delivery
    val deliveryPrice = payload.deliveryPrice
    val itemType = payload.itemType

    val item = itemController.createItem(
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      user = user,
      itemType = itemType,
      metadata = metadata,
      images = images,
      thumbnailUrl = thumbnailUrl,
      properties = itemProperties,
      price = price,
      priceUnit = priceUnit,
      paymentMethod = paymentMethod,
      delivery = delivery,
      deliveryPrice = deliveryPrice,
      creatorId = keycloakUserId
    )

    val searchHounds = searchHoundController.listSearchHounds(user = null, category = category, notificationsOn = true)
    searchHoundController.sendNotifications(searchHounds, item)
    return createOk(itemTranslator.translate(item))
  }

  override fun listItems(
    userId: UUID?,
    categoryId: UUID?,
    firstResult: Int?,
    maxResults: Int?,
    sortByDateReturnOldestFirst: Boolean?,
    includeExpired: Boolean?,
    itemType: ItemType?
  ): Response {

    if (!isAnonymous && !isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    var user: fi.metatavu.cityloops.persistence.model.User? = null
    if (userId != null) {
      user = userController.findUserById(userId) ?: return createNotFound("User with ID: $userId could not be found")
    }

    if (includeExpired !== null && includeExpired && (userId == null || userId != loggerUserId) && !isAdmin) {
      return createUnauthorized("Only admins can list other users expired items")
    }

    var category: fi.metatavu.cityloops.persistence.model.Category? = null
    if (categoryId != null) {
      category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID: $categoryId could not be found!")
    }

    val items = itemController.listItems(
      firstResult = firstResult,
      maxResults = maxResults,
      sortByDateReturnOldestFirst = sortByDateReturnOldestFirst,
      user = user,
      category = category,
      includeExpired = includeExpired,
      itemType = itemType
    )
    return createOk(items.map(itemTranslator::translate))
  }

  override fun findItem(itemId: UUID?): Response {
    itemId ?: return createBadRequest("Missing item ID")

    val item = itemController.findItemById(itemId) ?: return createNotFound("Item with ID: $itemId could not be found!")
    val expired = item.expired ?: false
    val userId = item.user?.id
    if (expired && (loggerUserId == null || userId == null || userId != loggerUserId)) {
      return createNotFound("Item with ID: $itemId is expired!")
    }
    return createOk(itemTranslator.translate(item))
  }

  override fun updateItem(itemId: UUID?, payload: Item?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    payload ?: return createBadRequest("Missing request body")
    itemId ?: return createBadRequest("Missing item ID")

    val foundItem = itemController.findItemById(itemId) ?: return createNotFound("Item with ID: $itemId could not be found!")
    var expiresAt = foundItem.expiresAt ?: OffsetDateTime.now().plusDays(30)
    val expired = payload.expired
    if (foundItem.expired == true && !expired) {
      //Item is renewed
      expiresAt = OffsetDateTime.now().plusDays(30)
    }
    val title = payload.title
    val categoryId = payload.category ?: return createBadRequest("Missing category for item")
    val category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID $categoryId not found")
    val onlyForCompanies = payload.onlyForCompanies
    val metadata =  payload.metadata
    val itemType = payload.itemType
    val images = payload.images
    val thumbnailUrl = payload.thumbnailUrl
    val itemProperties = payload.properties
    val price = payload.price
    val priceUnit = payload.priceUnit
    val paymentMethod = payload.paymentMethod
    val delivery = payload.delivery
    val deliveryPrice = payload.deliveryPrice

    val item = itemController.updateItem(
      item = foundItem,
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      metadata = metadata,
      itemType = itemType,
      images = images,
      thumbnailUrl = thumbnailUrl,
      properties = itemProperties,
      price = price,
      priceUnit = priceUnit,
      paymentMethod = paymentMethod,
      delivery = delivery,
      deliveryPrice = deliveryPrice,
      expired = expired,
      expiresAt = expiresAt,
      lastModifierId = keycloakUserId
    )

    val searchHounds = searchHoundController.listSearchHounds(user = null, category = category, notificationsOn = true)
    searchHoundController.sendNotifications(searchHounds, item)

    return createOk(itemTranslator.translate(item))
  }

  override fun deleteItem(itemId: UUID?): Response {
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }
    itemId ?: return createBadRequest("Missing item ID")

    val item = itemController.findItemById(itemId) ?: return createNotFound("Item with ID: $itemId could not be found!")
    itemController.deleteItem(item)
    return createNoContent()
  }

  companion object {
    private const val NOT_FOUND_MESSAGE = "Not found"
    private const val UNAUTHORIZED = "Unauthorized"
    private const val FORBIDDEN = "Forbidden"
  }
}