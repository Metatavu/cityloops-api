package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.PublicUsersApi
import fi.metatavu.cityloops.api.translate.PublicUserTranslator
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
class PublicUsersApiImpl: PublicUsersApi, AbstractApi() {

  @Inject
  private lateinit var userController: UserController

  @Inject
  private lateinit var publicUserTranslator: PublicUserTranslator

  override fun findPublicUser(userId: UUID?): Response {
    if (!isAnonymous && !isUser) {
      return createUnauthorized(FORBIDDEN)
    }

    userId ?: return createBadRequest("Missing user ID")

    val foundUser = userController.findUserById(id = userId) ?: return createNotFound("Could not find user with id: $userId")
    return createOk(publicUserTranslator.translate(foundUser))
  }

  companion object {
    private const val NOT_FOUND_MESSAGE = "Not found"
    private const val UNAUTHORIZED = "Unauthorized"
    private const val FORBIDDEN = "Forbidden"
  }

}