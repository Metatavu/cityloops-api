package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.CategoriesApi
import fi.metatavu.cityloops.api.spec.UsersApi
import fi.metatavu.cityloops.api.spec.model.User
import fi.metatavu.cityloops.api.translate.UserTranslator
import fi.metatavu.cityloops.controllers.KeycloakController
import fi.metatavu.cityloops.controllers.UserController
import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Users API REST endpoints
 *
 * @author Jari Nykänen
 */
@RequestScoped
@Stateful
class UsersApiImpl: UsersApi, AbstractApi() {

  @Inject
  private lateinit var userController: UserController

  @Inject
  private lateinit var userTranslator: UserTranslator

  @Inject
  private lateinit var keycloakController: KeycloakController

  override fun createUser(payload: User?): Response {

    if (!isAnonymous && !isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    payload ?: return createBadRequest("Missing request body")
    val email = payload.email
    val password = payload.password ?: return createBadRequest("Password is required!")

    if (keycloakController.findUserByEmail(email) != null) {
      return createBadRequest("User with given email $email already exists!")
    }

    val user = keycloakController.createUser(email, password)
    user ?: return createBadRequest("Failed to create user!")
    val keycloakId = user.id ?: return createInternalServerError("Keycloak user didn't have ID")

    val name = payload.name
    val address = payload.address
    val phoneNumber = payload.phoneNumber
    val companyAccount = payload.companyAccount
    val verified = payload.verified
    val companyId = payload.companyId
    val officeInfo = payload.officeInfo
    val coordinates = payload.coordinates
    val description = payload.description
    val logoUrl = payload.logoUrl

    val createdUser = userController.createUser(
      id = keycloakId,
      name = name,
      address = address,
      email = email,
      phoneNumber = phoneNumber,
      companyAccount = companyAccount,
      verified = verified,
      companyId = companyId,
      officeInfo = officeInfo,
      coordinates = coordinates,
      description = description,
      logoUrl = logoUrl
    )

    return createOk(userTranslator.translate(createdUser))
  }

  override fun listUsers(companyAccount: Boolean?, verified: Boolean?): Response? {
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    val users = userController.listUsers(companyAccount, verified)
    return createOk(users.map(userTranslator::translate))
  }

  override fun findUser(userId: UUID?): Response {
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }
    userId ?: return createBadRequest("Missing user ID")

    val foundUser = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")
    return createOk(userTranslator.translate(foundUser))
  }

  override fun updateUser(userId: UUID?, payload: User?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    userId ?: return createBadRequest("Missing user id")
    payload ?: return createBadRequest("Missing user payload")

    val foundUser = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")

    val name = payload.name
    val address = payload.address
    val email = payload.email
    val phoneNumber = payload.phoneNumber
    val companyAccount = payload.companyAccount
    val verified = payload.verified
    val companyId = payload.companyId
    val officeInfo = payload.officeInfo
    val coordinates = payload.coordinates
    val description = payload.description
    val logoUrl = payload.logoUrl

    val updatedUser = userController.updateUser(
      user = foundUser,
      name = name,
      address = address,
      email = email,
      phoneNumber = phoneNumber,
      companyAccount = companyAccount,
      verified = verified,
      modifierId = keycloakUserId,
      companyId = companyId,
      officeInfo = officeInfo,
      coordinates = coordinates,
      description = description,
      logoUrl = logoUrl
    )

    return createOk(userTranslator.translate(updatedUser))
  }

  override fun deleteUser(userId: UUID?): Response {
    if (!isUser) {
      return createUnauthorized(FORBIDDEN)
    }
    userId ?: return createBadRequest("Missing user ID")

    val user = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")
    keycloakController.deleteUser(user.id.toString())
    userController.deleteUser(user)
    return createNoContent()
  }

  companion object {
    private const val NOT_FOUND_MESSAGE = "Not found"
    private const val UNAUTHORIZED = "Unauthorized"
    private const val FORBIDDEN = "Forbidden"
  }

}