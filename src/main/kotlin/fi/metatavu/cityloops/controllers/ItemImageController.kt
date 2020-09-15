package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.persistence.dao.ItemImageDAO
import fi.metatavu.cityloops.persistence.model.Item
import fi.metatavu.cityloops.persistence.model.ItemImage
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for item images
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ItemImageController {

  @Inject
  private lateinit var itemImageDAO: ItemImageDAO

  /**
   * Creates new itemImage
   *
   * @param item item
   * @param imageUrl image url string
   * @return created item image
   */
  fun createItemImage(item: Item, imageUrl: String): ItemImage {
    return itemImageDAO.create(UUID.randomUUID(), item, imageUrl)
  }

  /**
   * Finds a item image by id
   *
   * @param id item image id
   * @return found item image or null if not found
   */
  fun findItemImageById(id: UUID): ItemImage? {
    return itemImageDAO.findById(id)
  }

  /**
   * List of itemImages
   *
   * @param item item
   * @return list of itemImages
   */
  fun listItemImages(item: Item): List<ItemImage> {
    return itemImageDAO.listImages(item)
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

    existingImages.forEach{ itemImage ->
      deleteItemImage(itemImage)
    }
  }

  /**
   * Deletes a itemImage
   *
   * @param itemImage itemImage to be deleted
   */
  fun deleteItemImage(itemImage: ItemImage) {
    itemImageDAO.delete(itemImage)
  }

}