package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.SearchHoundsApi
import fi.metatavu.cityloops.api.spec.model.SearchHound
import fi.metatavu.cityloops.api.translate.CategoryTranslator
import fi.metatavu.cityloops.api.translate.SearchHoundTranslator
import fi.metatavu.cityloops.controllers.CategoryController
import fi.metatavu.cityloops.controllers.SearchHoundController
import fi.metatavu.cityloops.controllers.UserController

import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Search hounds API REST endpoints
 *
 * @author Jari Nykänen
 */
@RequestScoped
@Stateful
class SearchHoundsApiImpl: SearchHoundsApi, AbstractApi() {

  @Inject
  private lateinit var categoryController: CategoryController

  @Inject
  private lateinit var categoryTranslator: CategoryTranslator

  @Inject
  private lateinit var searchHoundController: SearchHoundController

  @Inject
  private lateinit var searchHoundTranslator: SearchHoundTranslator

  @Inject
  private lateinit var userController: UserController

  override fun createSearchHound(payload: SearchHound?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser && !isAdmin) {
      return createUnauthorized(FORBIDDEN)
    }
    payload ?: return createBadRequest("Missing request body")

    val name = payload.name
    val notificationsOn = payload.notificationsOn
    val categoryId = payload.categoryId
    val category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID $categoryId not found")
    val userId = payload.userId
    val user = userController.findUserById(userId) ?: return createNotFound("User with ID $userId could not be found!")

    val expires =  payload.expires
    val minPrice = payload.minPrice
    val maxPrice = payload.maxPrice

    val searchHound = searchHoundController.createSearchHound(
      name = name,
      notificationsOn = notificationsOn,
      category = category,
      user = user,
      expires = expires,
      minPrice = minPrice,
      maxPrice = maxPrice,
      creatorId = keycloakUserId
    )

    return createOk(searchHoundTranslator.translate(searchHound))
  }

  override fun listSearchHounds(userId: UUID?, categoryId: UUID?, notificationOn: Boolean?): Response {

    if (!isUser && !isAdmin) {
      return createUnauthorized(FORBIDDEN)
    }

    var user: fi.metatavu.cityloops.persistence.model.User? = null
    if (userId != null) {
      user = userController.findUserById(userId) ?: return createNotFound("User with ID: $userId could not be found")
    }

    var category: fi.metatavu.cityloops.persistence.model.Category? = null
    if (categoryId != null) {
      category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID: $categoryId could not be found!")
    }

    val searchHounds = searchHoundController.listSearchHounds(user, category, null)
    return createOk(searchHounds.map(searchHoundTranslator::translate))
  }

  override fun findSearchHound(searchHoundId: UUID?): Response {
    if (!isUser && !isAdmin) {
      return createUnauthorized(FORBIDDEN)
    }

    searchHoundId ?: return createBadRequest("Missing searchHound ID")

    val searchHound = searchHoundController.findSearchHoundById(searchHoundId) ?: return createNotFound("SearchHound with ID: $searchHoundId could not be found!")
    return createOk(searchHoundTranslator.translate(searchHound))
  }

  override fun updateSearchHound(searchHoundId: UUID?, payload: SearchHound?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser && !isAdmin) {
      return createUnauthorized(FORBIDDEN)
    }

    payload ?: return createBadRequest("Missing request body")
    searchHoundId ?: return createBadRequest("Missing searchHound ID")

    val foundSearchHound = searchHoundController.findSearchHoundById(searchHoundId) ?: return createNotFound("SearchHound with ID: $searchHoundId could not be found!")
    val name = payload.name
    val notificationsOn = payload.notificationsOn
    val categoryId = payload.categoryId
    val category = categoryController.findCategoryById(categoryId) ?: return createNotFound("Category with ID $categoryId not found")
    val userId = payload.userId
    userController.findUserById(userId) ?: return createNotFound("User with ID $userId could not be found!")
    val expires =  payload.expires
    val minPrice = payload.minPrice
    val maxPrice = payload.maxPrice

    val searchHound = searchHoundController.updateSearchHound(
      searchHound = foundSearchHound,
      name = name,
      notificationsOn = notificationsOn,
      category = category,
      expires = expires,
      minPrice = minPrice,
      maxPrice = maxPrice,
      lastModifierId = keycloakUserId
    )

    return createOk(searchHoundTranslator.translate(searchHound))
  }

  override fun deleteSearchHound(searchHoundId: UUID?): Response {
    if (!isUser && !isAdmin) {
      return createUnauthorized(FORBIDDEN)
    }
    searchHoundId ?: return createBadRequest("Missing searchHound ID")

    val searchHound = searchHoundController.findSearchHoundById(searchHoundId) ?: return createNotFound("SearchHound with ID: $searchHoundId could not be found!")
    searchHoundController.deleteSearchHound(searchHound)
    return createNoContent()
  }

  companion object {
    private const val NOT_FOUND_MESSAGE = "Not found"
    private const val UNAUTHORIZED = "Unauthorized"
    private const val FORBIDDEN = "Forbidden"
  }
}