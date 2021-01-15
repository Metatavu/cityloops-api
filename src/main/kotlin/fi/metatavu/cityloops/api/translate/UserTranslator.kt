package fi.metatavu.cityloops.api.translate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fi.metatavu.cityloops.api.spec.model.Coordinates
import fi.metatavu.cityloops.api.spec.model.ItemProperty
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for translating JPA user entities into REST resources
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class UserTranslator: AbstractTranslator<fi.metatavu.cityloops.persistence.model.User, fi.metatavu.cityloops.api.spec.model.User>() {

  override fun translate(entity: fi.metatavu.cityloops.persistence.model.User): fi.metatavu.cityloops.api.spec.model.User {
    val result = fi.metatavu.cityloops.api.spec.model.User()
    result.id = entity.id
    result.name = entity.name
    result.address = entity.address
    result.email = entity.email
    result.phoneNumber = entity.phoneNumber
    result.companyAccount = entity.companyAccount
    result.verified = entity.verified
    result.companyId = entity.companyId
    result.officeInfo = entity.officeInfo
    result.coordinates = getCoordinates(entity.coordinates)
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