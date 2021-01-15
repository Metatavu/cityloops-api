package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.cityloops.api.spec.model.Coordinates
import fi.metatavu.cityloops.persistence.dao.UserDAO
import fi.metatavu.cityloops.persistence.model.User
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for users
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class UserController {

  @Inject
  private lateinit var userDAO: UserDAO

  /**
   * Creates new user
   *
   * @param id keycloak id
   * @param name user name
   * @param address user address
   * @param email user email
   * @param phoneNumber user phone number
   * @param companyAccount is this user used by company
   * @param verified is this user verified
   * @param companyId company id
   * @param officeInfo office info
   * @param coordinates office coordinates
   * @return created user
   */
  fun createUser(
    id: String,
    name: String,
    address: String,
    email: String,
    phoneNumber: String,
    companyAccount: Boolean,
    verified: Boolean,
    companyId: String?,
    officeInfo: String?,
    coordinates: Coordinates?
  ): User {
    val keycloakId = UUID.fromString(id)
    return userDAO.create(
      id = keycloakId,
      name = name,
      address = address,
      email = email,
      phoneNumber = phoneNumber,
      companyAccount = companyAccount,
      verified = verified,
      companyId = companyId,
      officeInfo = officeInfo,
      coordinates = getDataAsString(coordinates),
      creatorId = keycloakId
    )
  }

  /**
   * Finds a user by id
   *
   * @param id user id
   * @return found user or null if not found
   */
  fun findUserById(id: UUID): User? {
    return userDAO.findById(id)
  }

  /**
   * List of categories
   *
   * @param companyAccount list only company accounts
   * @param verified list only verified accounts
   * @return list of categories
   */
  fun listUsers(companyAccount: Boolean?, verified: Boolean?): List<User> {
    return userDAO.list(companyAccount, verified)
  }

  /**
   * Updates a user
   *
   * @param user user
   * @param name user name
   * @param address user address
   * @param email user email
   * @param phoneNumber user phone number
   * @param companyAccount is this user used by company
   * @param verified us this user verified
   * @param companyId company id
   * @param officeInfo office info
   * @param coordinates office coordinates
   * @param modifierId modifying user id
   * @return updated user
   */
  fun updateUser(
    user: User,
    name: String,
    address: String,
    email: String,
    phoneNumber: String,
    companyAccount: Boolean,
    verified: Boolean,
    companyId: String?,
    officeInfo: String?,
    coordinates: Coordinates?,
    modifierId: UUID
  ): User {
    val result = userDAO.updateName(user, name, modifierId)
    userDAO.updateAddress(result, address, modifierId)
    userDAO.updateEmail(result, email, modifierId)
    userDAO.updatePhoneNumber(result, phoneNumber, modifierId)
    userDAO.updateCompanyAccount(result, companyAccount, modifierId)
    userDAO.updateVerified(result, verified, modifierId)
    userDAO.updateCompanyId(result, companyId, modifierId)
    userDAO.updateOfficeInfo(result, officeInfo, modifierId)
    userDAO.updateCoordinates(result, getDataAsString(coordinates), modifierId)
    return result
  }

  /**
   * Deletes a user
   *
   * @param user user to be deleted
   */
  fun deleteUser(user: User) {
    return userDAO.delete(user)
  }

  /**
   * Serializes the object into JSON string
   *
   * @param data object
   * @return JSON string
   */
  private fun <T> getDataAsString(data: T): String? {
    val objectMapper = ObjectMapper()
    return objectMapper.writeValueAsString(data)
  }

}
