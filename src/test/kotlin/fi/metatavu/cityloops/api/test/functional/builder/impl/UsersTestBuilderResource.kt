package fi.metatavu.cityloops.api.test.functional.builder.impl

import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.client.apis.UsersApi
import fi.metatavu.cityloops.api.client.infrastructure.ClientException
import fi.metatavu.cityloops.api.client.models.User
import fi.metatavu.cityloops.api.test.functional.settings.TestSettings
import fi.metatavu.cityloops.api.test.functional.impl.ApiTestBuilderResource
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import org.junit.Assert
import java.util.*

class UsersTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<User, ApiClient>(testBuilder, apiClient) {
  override fun getApi(): UsersApi {
    ApiClient.accessToken = accessTokenProvider?.accessToken
    return UsersApi(TestSettings.apiBasePath)
  }

  /**
   * List users
   *
   * @param companyAccount list only company accounts
   * @param verified list only verified accounts
   * @return list of users
   */
  fun list(companyAccount: Boolean?, verified: Boolean?): Array<User> {
    return api.listUsers(companyAccount, verified)
  }
  /**
   * Creates new user with default values
   *
   * @return created user
   */
  fun create(): User {
    val user = User(
      name = "Default name",
      address = "Default address",
      email = "example@email.com",
      phoneNumber = "1234567890",
      companyAccount = false,
      verified = false
    )
    return create(user)
  }

  /**
   * Create new user with custom values
   *
   * @param user user with custom values
   * @return created User
   */
  fun create(user: User): User {
    val createdUser = api.createUser(user)
    addClosable(createdUser)
    return createdUser
  }

  /**
   * Find user with id
   *
   * @param userId id of the user
   * @return found user
   */
  fun findUser(userId: UUID): User {
    return api.findUser(userId)
  }

  /**
   * Update user
   *
   * @param payload user to update
   * @return updated user
   */
  fun updateUser(userId: UUID, payload: User): User? {
    return api.updateUser(userId, payload)
  }

  /**
   * Deletes a user from the API and removes closable
   *
   * @param userId user id to be deleted
   */
  fun delete(userId: UUID) {
    api.deleteUser(userId)
    removeCloseable{ closable: Any ->
      if (closable !is User) {
        return@removeCloseable false
      }

      val closeableUser: User = closable
      closeableUser.id!!.equals(userId)
    }
  }

  /**
   * Find that should fail with given status
   *
   * @param expectedStatus expected status code from server
   * @param userId user id
   */
  fun assertFindFail(expectedStatus: Int, userId: UUID) {
    try {
      api.findUser(userId)
      Assert.fail(String.format("Expected find to fail with message %d", expectedStatus))
    } catch (e: ClientException) {
      assertClientExceptionStatus(expectedStatus, e)
    }
  }

  /**
   * Clean resources
   *
   * @param user user to be cleaned
   */
  override fun clean(user: User) {
    this.api.deleteUser(user.id!!)
  }
}
