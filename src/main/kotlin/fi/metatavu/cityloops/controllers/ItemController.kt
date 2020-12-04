package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.api.spec.model.ItemProperty
import fi.metatavu.cityloops.api.spec.model.Metadata
import fi.metatavu.cityloops.persistence.dao.ItemDAO
import fi.metatavu.cityloops.persistence.dao.ItemImageDAO
import fi.metatavu.cityloops.persistence.model.Category
import fi.metatavu.cityloops.persistence.model.Item
import fi.metatavu.cityloops.persistence.model.User
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

  /**
   * Creates new item
   *
   * @param title item title
   * @param category category this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param user item owner
   * @param metadata item metadata
   * @param images list of images
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs
   * @param price price of the item
   * @param priceUnit price unit of the item
   * @param creatorId creator's id
   * @return created item
   */
  fun createItem(
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    user: User,
    metadata: Metadata,
    images: List<String>?,
    thumbnailUrl: String?,
    properties: List<ItemProperty>?,
    price: Double,
    priceUnit: String,
    creatorId: UUID
  ): Item {
    val item = itemDAO.create(
      id = UUID.randomUUID(),
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      user = user,
      metadata = getDataAsString(metadata),
      thumbnailUrl = thumbnailUrl,
      properties = getDataAsString(properties),
      price = price,
      priceUnit = priceUnit,
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
   * List of categories
   *
   * @param firstResult index of the first result
   * @param maxResults limit amount of results to this number
   * @param returnOldestFirst return oldest result first
   * @param user filter by user
   * @param category filter by category
   *
   * @return list of categories
   */
  fun listItems(firstResult: Int?, maxResults: Int?, returnOldestFirst: Boolean?, user: User?, category: Category?): List<Item> {
    return itemDAO.list(firstResult, maxResults, returnOldestFirst, user, category)
  }

  /**
   * Updates a item
   *
   * @param item item to be updated
   * @param title item title
   * @param category category where this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param metadata item metadata
   * @param images list of images
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs
   * @param price price of the item
   * @param priceUnit price unit of the item
   * @param lastModifierId last modifier user id
   * @return updated item
   */
  fun updateItem(
    item: Item,
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    metadata: Metadata,
    images: List<String>?,
    thumbnailUrl: String?,
    properties: List<ItemProperty>?,
    price: Double,
    priceUnit: String,
    lastModifierId: UUID
  ): Item {
    val result = itemDAO.updateTitle(item, title, lastModifierId)
    itemDAO.updateCategory(result, category, lastModifierId)
    itemDAO.updateOnlyForCompanies(result, onlyForCompanies, lastModifierId)
    itemDAO.updateMetadata(result, getDataAsString(metadata), lastModifierId)
    itemDAO.updateThumbnailUrl(result, thumbnailUrl, lastModifierId)
    itemDAO.updateProperties(result, getDataAsString(properties), lastModifierId)
    itemDAO.updatePrice(result, price, lastModifierId)
    itemDAO.updatePriceUnit(result, priceUnit, lastModifierId)
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
