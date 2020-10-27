package fi.metatavu.cityloops.api.translate

import com.fasterxml.jackson.databind.ObjectMapper
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
    result.keycloakId = entity.keycloakId
    result.name = entity.name
    result.address = entity.address
    result.email = entity.email
    result.phoneNumber = entity.phoneNumber
    result.companyAccount = entity.companyAccount
    result.verified = entity.verified
    result.creatorId = entity.creatorId
    result.lastModifierId = entity.lastModifierId
    result.createdAt = entity.createdAt
    result.modifiedAt = entity.modifiedAt

    return result
  }

}