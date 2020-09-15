package fi.metatavu.cityloops.api.translate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fi.metatavu.cityloops.api.spec.model.ItemProperty
import fi.metatavu.cityloops.api.spec.model.Metadata
import fi.metatavu.cityloops.controllers.ItemImageController
import fi.metatavu.cityloops.persistence.dao.ItemImageDAO
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.streams.toList

/**
 * Translator for translating JPA item entities into REST resources
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ItemTranslator: AbstractTranslator<fi.metatavu.cityloops.persistence.model.Item, fi.metatavu.cityloops.api.spec.model.Item>() {

  @Inject
  private lateinit var itemImageController: ItemImageController

  override fun translate(entity: fi.metatavu.cityloops.persistence.model.Item): fi.metatavu.cityloops.api.spec.model.Item {
    val result = fi.metatavu.cityloops.api.spec.model.Item()

    val imagesIds = itemImageController.listItemImages(entity).stream()
      .map { it.url!! }
      .toList()

    result.id = entity.id
    result.title = entity.title
    result.category = entity.category?.id
    result.onlyForCompanies = entity.onlyForCompanies
    result.metadata = getMetadata(entity.metadata)
    result.images = imagesIds
    result.thumbnailUrl = entity.thumbnailUrl
    result.properties = getItemProperties(entity.properties)
    result.creatorId = entity.creatorId
    result.lastModifierId = entity.lastModifierId
    result.createdAt = entity.createdAt
    result.modifiedAt = entity.modifiedAt

    return result
  }

  /**
   * Reads item properties string as item property spec object
   *
   * @param data string data
   * @return event triggers object
   */
  private fun getItemProperties(data: String?): List<ItemProperty> {
    data ?: return listOf()
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(data)
  }

  /**
   * Reads metadata string as metadata spec object
   *
   * @param data string data
   * @return metadata object
   */
  private fun getMetadata(data: String?): Metadata {
    data ?: return Metadata()
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(data)
  }

}