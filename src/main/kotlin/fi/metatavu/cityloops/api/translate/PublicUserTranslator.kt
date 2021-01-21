package fi.metatavu.cityloops.api.translate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fi.metatavu.cityloops.api.spec.model.Coordinates
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for translating JPA user entities into REST PublicUser resources
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class PublicUserTranslator: AbstractTranslator<fi.metatavu.cityloops.persistence.model.User, fi.metatavu.cityloops.api.spec.model.PublicUser>() {

  override fun translate(entity: fi.metatavu.cityloops.persistence.model.User): fi.metatavu.cityloops.api.spec.model.PublicUser {
    val result = fi.metatavu.cityloops.api.spec.model.PublicUser()
    result.id = entity.id
    result.name = entity.name
    result.address = entity.address
    result.email = entity.email
    result.phoneNumber = entity.phoneNumber
    result.companyId = entity.companyId
    result.officeInfo = entity.officeInfo
    result.coordinates = getCoordinates(entity.coordinates)
    result.description = entity.description
    result.logoUrl = entity.logoUrl
    result.creatorId = entity.creatorId
    result.lastModifierId = entity.lastModifierId
    result.createdAt = entity.createdAt
    result.modifiedAt = entity.modifiedAt

    return result
  }

  /**
   * Reads user coordinates string as user coordinates spec object
   *
   * @param data string data
   * @return coordinates object
   */
  private fun getCoordinates(data: String?): Coordinates? {
    data ?: return null
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(data)
  }

}