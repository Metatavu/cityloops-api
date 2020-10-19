package fi.metatavu.cityloops.api.translate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fi.metatavu.cityloops.api.spec.model.CategoryProperty
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for translating JPA category entities into REST resources
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class CategoryTranslator: AbstractTranslator<fi.metatavu.cityloops.persistence.model.Category, fi.metatavu.cityloops.api.spec.model.Category>() {

  override fun translate(entity: fi.metatavu.cityloops.persistence.model.Category): fi.metatavu.cityloops.api.spec.model.Category {
    val result = fi.metatavu.cityloops.api.spec.model.Category()
    result.id = entity.id
    result.name = entity.name
    result.parentCategoryId = entity.parentCategoryId
    result.properties = getCategoryProperties(entity.properties)
    result.creatorId = entity.creatorId
    result.lastModifierId = entity.lastModifierId
    result.createdAt = entity.createdAt
    result.modifiedAt = entity.modifiedAt

    return result
  }

  /**
   * Reads category properties string as category property spec object
   *
   * @param data string data
   * @return event triggers object
   */
  private fun getCategoryProperties(data: String?): List<CategoryProperty> {
    data ?: return listOf()
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(data)
  }

}