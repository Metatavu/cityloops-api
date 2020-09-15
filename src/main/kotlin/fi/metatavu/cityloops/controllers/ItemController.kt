package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.api.spec.model.ItemProperty
import fi.metatavu.cityloops.api.spec.model.Metadata
import fi.metatavu.cityloops.persistence.dao.ItemDAO
import fi.metatavu.cityloops.persistence.dao.ItemImageDAO
import fi.metatavu.cityloops.persistence.model.Category
import fi.metatavu.cityloops.persistence.model.Item
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
   * @param category category where this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param metadata item metadata
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs
   * @param creatorId creator's id
   * @return created item
   */
  fun createItem(
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    metadata: Metadata,
    thumbnailUrl: String?,
    properties: List<ItemProperty>?,
    creatorId: UUID
  ): Item {
    return itemDAO.create(
      id = UUID.randomUUID(),
      title = title,
      category = category,
      onlyForCompanies = onlyForCompanies,
      metadata = getDataAsString(metadata),
      thumbnailUrl = thumbnailUrl,
      properties = getDataAsString(properties),
      creatorId = creatorId
    )
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
   * @return list of categories
   */
  fun listItems(): List<Item> {
    return itemDAO.listAll()
  }

  /**
   * Updates a item
   *
   * @param item item to be updated
   * @param title item title
   * @param category category where this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param metadata item metadata
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs
   * @param lastModifierId last modifier user id
   * @return updated item
   */
  fun updateItem(
    item: Item,
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    metadata: Metadata,
    thumbnailUrl: String?,
    properties: List<ItemProperty>?,
    lastModifierId: UUID
  ): Item {
    val result = itemDAO.updateTitle(item, title, lastModifierId)
    itemDAO.updateCategory(result, category, lastModifierId)
    itemDAO.updateOnlyForCompanies(result, onlyForCompanies, lastModifierId)
    itemDAO.updateMetadata(result, getDataAsString(metadata), lastModifierId)
    itemDAO.updateThumbnailUrl(result, thumbnailUrl, lastModifierId)
    itemDAO.updateProperties(result, getDataAsString(properties), lastModifierId)
    return result
  }

  /**
   * Sets item images
   *
   * @param item item where images belong to
   * @param imageUrls list of image urls
   */
  fun setItemImages(item: Item, imageUrls: List<String>) {
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
    return itemDAO.delete(item)
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