package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.api.spec.model.ItemProperty
import fi.metatavu.cityloops.api.spec.model.ItemType
import fi.metatavu.cityloops.api.spec.model.Metadata
import fi.metatavu.cityloops.notifications.NotificationController
import fi.metatavu.cityloops.persistence.dao.ItemDAO
import fi.metatavu.cityloops.persistence.dao.ItemImageDAO
import fi.metatavu.cityloops.persistence.model.Category
import fi.metatavu.cityloops.persistence.model.Item
import fi.metatavu.cityloops.persistence.model.User
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for items
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ItemController {

  @Inject
  private lateinit var itemDAO: ItemDAO

  @Inject
  private lateinit var itemImageDAO: ItemImageDAO

  @Inject
  private lateinit var itemImageController: ItemImageController

  @Inject
  private lateinit var notificationController: NotificationController

  /**
   * Creates new item
   *
   * @param title item title
   * @param category category this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param user item owner
   * @param metadata item metadata
   * @param itemType item type
   * @param images list of images
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs
   * @param price price of the item
   * @param priceUnit price unit of the item
   * @param paymentMethod item payment method
   * @param delivery is item deliverable
   * @param deliveryPrice delivery price
   * @param creatorId creator's id
   * @return created item
   */
  fun createItem(
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    user: User,
    metadata: Metadata,
    itemType: ItemType,
    images: List<String>?,
    thumbnailUrl: String?,
    properties: List<ItemProperty>?,
    price: String,
    priceUnit: String,
    paymentMethod: String,
    delivery: Boolean,
    deliveryPrice: Double?,
    creatorId: UUID
  ): Item {
    val item = itemDAO.create(
      id = UUID.randomUUID(),
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      user = user,
      metadata = getDataAsString(metadata),
      itemType = itemType,
      thumbnailUrl = thumbnailUrl,
      properties = getDataAsString(properties),
      price = price,
      priceUnit = priceUnit,
      paymentMethod = paymentMethod,
      delivery = delivery,
      deliveryPrice = deliveryPrice,
      creatorId = creatorId
    )

    setItemImages(item, images)
    return item
  }

  /**
   * Finds a item by id
   *
   * @param id item id
   * @return found item or null if not found
   */
  fun findItemById(id: UUID): Item? {
    return itemDAO.findById(id)
  }

  /**
   * List of items
   *
   * @param firstResult index of the first result
   * @param maxResults limit amount of results to this number
   * @param sortByDateReturnOldestFirst return oldest result first
   * @param user filter by user
   * @param category filter by category
   * @param itemType filter by item type
   *
   * @return list of items
   */
  fun listItems(
    firstResult: Int?,
    maxResults: Int?,
    sortByDateReturnOldestFirst: Boolean?,
    user: User?,
    category: Category?,
    includeExpired: Boolean?,
    itemType: ItemType?
  ): List<Item> {
    return itemDAO.list(
      firstResult = firstResult,
      maxResults = maxResults,
      sortByDateReturnOldestFirst = sortByDateReturnOldestFirst,
      user = user,
      category = category,
      includeExpired = includeExpired,
      itemType = itemType
    )
  }

  /**
   * Lists items to expire
   *
   * @return list of items to expire
   */
  fun listItemsToExpire(): List<Item> {
    return itemDAO.listItemsToExpire()
  }

  /**
   * Marks item as expired and sends notification by email
   *
   * @return item marked as expired
   */
  fun expireItem(item: Item): Item {
    notificationController.sendItemExpirationNotification(item)
    return itemDAO.updateExpires(item, true, item.lastModifierId)
  }

  /**
   * Updates a item
   *
   * @param item item to be updated
   * @param title item title
   * @param category category where this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param metadata item metadata
   * @param itemType item type
   * @param images list of images
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs
   * @param price price of the item
   * @param priceUnit price unit of the item
   * @param paymentMethod item payment method
   * @param delivery is item deliverable
   * @param deliveryPrice delivery price
   * @param lastModifierId last modifier user id
   * @return updated item
   */
  fun updateItem(
    item: Item,
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    metadata: Metadata,
    itemType: ItemType,
    images: List<String>?,
    thumbnailUrl: String?,
    properties: List<ItemProperty>?,
    price: String,
    priceUnit: String,
    paymentMethod: String,
    delivery: Boolean,
    deliveryPrice: Double?,
    expired: Boolean,
    expiresAt: OffsetDateTime,
    lastModifierId: UUID
  ): Item {
    val result = itemDAO.updateTitle(item, title, lastModifierId)
    itemDAO.updateCategory(result, category, lastModifierId)
    itemDAO.updateOnlyForCompanies(result, onlyForCompanies, lastModifierId)
    itemDAO.updateMetadata(result, getDataAsString(metadata), lastModifierId)
    itemDAO.updateItemType(result, itemType, lastModifierId)
    itemDAO.updateThumbnailUrl(result, thumbnailUrl, lastModifierId)
    itemDAO.updateProperties(result, getDataAsString(properties), lastModifierId)
    itemDAO.updatePrice(result, price, lastModifierId)
    itemDAO.updatePriceUnit(result, priceUnit, lastModifierId)
    itemDAO.updatePaymentMethod(result, paymentMethod, lastModifierId)
    itemDAO.updateDelivery(result, delivery, lastModifierId)
    itemDAO.updateDeliveryPrice(result, deliveryPrice, lastModifierId)
    itemDAO.updateExpires(result, expired, lastModifierId)
    itemDAO.updateExpiresAt(result, expiresAt, lastModifierId)
    setItemImages(result, images)
    return result
  }

  /**
   * Sets item images
   *
   * @param item item where images belong to
   * @param imageUrls list of image urls
   */
  fun setItemImages(item: Item, imageUrls: List<String>?) {
    imageUrls ?: return
    val existingImages = itemImageDAO.listImages(item).toMutableList()

    for (imageUrl in imageUrls) {
      val existingImage = existingImages.find { it.url == imageUrl }
      if (existingImage == null) {
        itemImageDAO.create(UUID.randomUUID(), item, imageUrl)
      } else {
        existingImages.remove(existingImage)
      }
    }

    existingImages.forEach(itemImageDAO::delete)
  }

  /**
   * Delete item images
   *
   * @param item item
   */
  fun deleteItemImages(item: Item) {
    itemImageController.listItemImages(item).forEach(itemImageController::deleteItemImage)
  }

  /**
   * Deletes a item
   *
   * @param item item to be deleted
   */
  fun deleteItem(item: Item) {
    deleteItemImages(item)
    itemDAO.delete(item)
  }

  /**
   * Serializes the object into JSON string
   *
   * @param data object
   * @return JSON string
   */
  private fun <T> getDataAsString(data: T): String {
    val objectMapper = ObjectMapper()
    return objectMapper.writeValueAsString(data)
  }

}
