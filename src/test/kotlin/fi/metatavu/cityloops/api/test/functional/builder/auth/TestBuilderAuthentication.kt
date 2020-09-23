package fi.metatavu.cityloops.api.test.functional.auth

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.test.functional.builder.impl.CategoriesTestBuilderResource
import fi.metatavu.cityloops.api.test.functional.builder.impl.ItemsTestBuilderResource
import fi.metatavu.cityloops.api.test.functional.builder.impl.UsersTestBuilderResource
import fi.metatavu.cityloops.api.test.functional.settings.TestSettings

/**
 * Test builder authentication
 *
 * @author Jari Nykänen
 *
 * Constructor
 *
 * @param testBuilder test builder instance
 * @param accessTokenProvider access token provider
 */
class TestBuilderAuthentication(testBuilder: AbstractTestBuilder<ApiClient>, accessTokenProvider: AccessTokenProvider) : AuthorizedTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

  private var accessTokenProvider: AccessTokenProvider? = accessTokenProvider
  private var categories: CategoriesTestBuilderResource? = null
  private var items: ItemsTestBuilderResource? = null
  private var users: UsersTestBuilderResource? = null

  /**
   * Creates a API client
   *
   * @param accessToken access token
   * @return API client
   */
  override fun createClient(accessToken: String): ApiClient {
    val result = ApiClient(TestSettings.apiBasePath)
    ApiClient.accessToken = accessToken
    return result
  }

  /**
   * Returns a test builder resource for categories
   *
   * @return test builder resource for categories
   */
  fun categories(): CategoriesTestBuilderResource {
    if (categories == null) {
      categories = CategoriesTestBuilderResource(testBuilder, accessTokenProvider, createClient())
    }

    return categories!!
  }

  /**
   * Returns a test builder resource for items
   *
   * @return test builder resource for items
   */
  fun items(): ItemsTestBuilderResource {
    if (items == null) {
      items = ItemsTestBuilderResource(testBuilder, accessTokenProvider, createClient())
    }

    return items!!
  }

  /**
   * Returns a test builder resource for users
   *
   * @return test builder resource for users
   */
  fun users(): UsersTestBuilderResource {
    if (users == null) {
      users = UsersTestBuilderResource(testBuilder, accessTokenProvider, createClient())
    }

    return users!!
  }

}