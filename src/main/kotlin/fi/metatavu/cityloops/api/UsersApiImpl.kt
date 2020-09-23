package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.CategoriesApi
import fi.metatavu.cityloops.api.spec.UsersApi
import fi.metatavu.cityloops.api.spec.model.User
import fi.metatavu.cityloops.api.translate.UserTranslator
import fi.metatavu.cityloops.controllers.UserController
import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Categories API REST endpoints
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

  override fun createUser(payload: User?): Response {
    val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    payload ?: return createBadRequest("Missing request body")

    val name = payload.name
    val address = payload.address
    val email = payload.email
    val phoneNumber = payload.phoneNumber
    val companyAccount = payload.companyAccount
    val verified = payload.verified

    val createdUser = userController.createUser(
      name = name,
      address = address,
      email = email,
      phoneNumber = phoneNumber,
      companyAccount = companyAccount,
      verified = verified,
      creatorId = userId
    )

    return createOk(userTranslator.translate(createdUser))
  }

  override fun listUsers(companyAccount: Boolean?, verified: Boolean?): Response? {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)

    val users = userController.listUsers(companyAccount, verified)
    return createOk(users.map(userTranslator::translate))
  }

  override fun findUser(userId: UUID?): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    userId ?: return createBadRequest("Missing user ID")

    val foundUser = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")
    return createOk(userTranslator.translate(foundUser))
  }

  override fun updateUser(userId: UUID?, payload: User?): Response {
    val keycloakUserId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    userId ?: return createBadRequest("Missing user id")
    payload ?: return createBadRequest("Missing user payload")

    val foundUser = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")

    val name = payload.name
    val address = payload.address
    val email = payload.email
    val phoneNumber = payload.phoneNumber
    val companyAccount = payload.companyAccount
    val verified = payload.verified

    val updatedUser = userController.updateUser(
      user = foundUser,
      name = name,
      address = address,
      email = email,
      phoneNumber = phoneNumber,
      companyAccount = companyAccount,
      verified = verified,
      modifierId = keycloakUserId
    )

    return createOk(userTranslator.translate(updatedUser))
  }

  override fun deleteUser(userId: UUID?): Response {
    loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
    userId ?: return createBadRequest("Missing user ID")

    val user = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")
    userController.deleteUser(user)
    return createNoContent()
  }

}