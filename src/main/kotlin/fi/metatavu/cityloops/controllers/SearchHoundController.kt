package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.api.spec.model.Metadata
import fi.metatavu.cityloops.notifications.NotificationController
import fi.metatavu.cityloops.persistence.dao.SearchHoundDAO
import fi.metatavu.cityloops.persistence.model.Category
import fi.metatavu.cityloops.persistence.model.Item
import fi.metatavu.cityloops.persistence.model.SearchHound
import fi.metatavu.cityloops.persistence.model.User
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for search hounds
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class SearchHoundController {

  @Inject
  private lateinit var searchHoundDAO: SearchHoundDAO

  @Inject
  private lateinit var notificationController: NotificationController

  /**
   * Creates new searchHound
   *
   * @param name search hound name
   * @param notificationsOn are notifications on or not
   * @param category category that is monitored
   * @param user search hound owner
   * @param expires when this search hound expires
   * @param minPrice min price of the search hound
   * @param maxPrice max price of the search hound
   * @param creatorId creator's id
   * @return created search hound
   */
  fun createSearchHound(
    name: String,
    notificationsOn: Boolean,
    category: Category,
    user: User,
    expires: OffsetDateTime,
    minPrice: Double?,
    maxPrice: Double?,
    creatorId: UUID
  ): SearchHound {
    return searchHoundDAO.create(
      id = UUID.randomUUID(),
      name = name,
      notificationsOn = notificationsOn,
      category = category,
      user = user,
      expires = expires,
      minPrice = minPrice,
      maxPrice = maxPrice,
      creatorId = creatorId
    )
  }

  /**
   * Finds a search hound by id
   *
   * @param id search hound id
   * @return found search hound or null if not found
   */
  fun findSearchHoundById(id: UUID): SearchHound? {
    return searchHoundDAO.findById(id)
  }

  /**
   * List of search hounds
   *
   * @param user filter by user
   * @param category filter by category
   * @param notificationsOn are notification on or not
   * @return list of categories
   */
  fun listSearchHounds(user: User?, category: Category?, notificationsOn: Boolean?): List<SearchHound> {
    return searchHoundDAO.list(user, category, notificationsOn)
  }

  /**
   * Updates search hound
   *
   * @param searchHound search hound to update
   * @param name search hound name
   * @param category category that is monitored
   * @param notificationsOn are notifications on or not
   * @param expires when this search hound expires
   * @param minPrice min price of the search hound
   * @param maxPrice max price of the search hound
   * @param lastModifierId last modifier id
   * @return created search hound
   */
  fun updateSearchHound(
    searchHound: SearchHound,
    name: String,
    notificationsOn: Boolean,
    category: Category,
    expires: OffsetDateTime,
    minPrice: Double?,
    maxPrice: Double?,
    lastModifierId: UUID
  ): SearchHound {
    val result = searchHoundDAO.updateName(searchHound, name, lastModifierId)
    searchHoundDAO.updateNotificationsOn(result, notificationsOn, lastModifierId)
    searchHoundDAO.updateCategory(result, category, lastModifierId)
    searchHoundDAO.updateExpires(result, expires, lastModifierId)
    searchHoundDAO.updateMinPrice(result, minPrice, lastModifierId)
    searchHoundDAO.updateMaxPrice(result, maxPrice, lastModifierId)
    return result
  }

  /**
   * Deletes a searchHound
   *
   * @param searchHound searchHound to be deleted
   */
  fun deleteSearchHound(searchHound: SearchHound) {
    searchHoundDAO.delete(searchHound)
  }

  /**
   * Sends notification email to users that have search hounds enabled
   *
   * @param searchHounds list of search hounds
   * @param item item
   */
  fun sendNotifications(searchHounds: List<SearchHound>, item: Item) {
    searchHounds.forEach { hound ->
      val user = hound.user
      if (user != null) {
        val email = user.email
        if (email != null) {
          notificationController.sendSearchHoundItemFoundNotification(email, item)
        }
      }
    }
  }

}
