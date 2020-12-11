package fi.metatavu.cityloops.api.translate

import javax.enterprise.context.ApplicationScoped

/**
 * Translator for translating JPA search hound entities into REST resources
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class SearchHoundTranslator: AbstractTranslator<fi.metatavu.cityloops.persistence.model.SearchHound, fi.metatavu.cityloops.api.spec.model.SearchHound>() {

  override fun translate(entity: fi.metatavu.cityloops.persistence.model.SearchHound): fi.metatavu.cityloops.api.spec.model.SearchHound {
    val result = fi.metatavu.cityloops.api.spec.model.SearchHound()

    result.id = entity.id
    result.name = entity.name
    result.notificationsOn = entity.notificationsOn
    result.categoryId = entity.category?.id
    result.userId = entity.user?.id
    result.expires = entity.expires
    result.minPrice = entity.minPrice
    result.maxPrice = entity.maxPrice
    result.creatorId = entity.creatorId
    result.lastModifierId = entity.lastModifierId
    result.createdAt = entity.createdAt
    result.modifiedAt = entity.modifiedAt

    return result
  }

}