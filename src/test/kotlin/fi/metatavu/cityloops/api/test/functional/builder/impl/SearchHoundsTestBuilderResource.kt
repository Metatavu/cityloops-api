package fi.metatavu.cityloops.api.test.functional.builder.impl

import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.client.apis.SearchHoundsApi
import fi.metatavu.cityloops.api.client.infrastructure.ClientException
import fi.metatavu.cityloops.api.client.models.SearchHound
import fi.metatavu.cityloops.api.test.functional.settings.TestSettings
import fi.metatavu.cityloops.api.test.functional.impl.ApiTestBuilderResource
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import org.junit.Assert
import java.time.OffsetDateTime
import java.util.*

class SearchHoundsTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<SearchHound, ApiClient>(testBuilder, apiClient) {
  override fun getApi(): SearchHoundsApi {
    ApiClient.accessToken = accessTokenProvider?.accessToken
    return SearchHoundsApi(TestSettings.apiBasePath)
  }

  /**
   * List search hounds
   *
   * @param userId filter by user
   * @param categoryId filter by category
   * @param notificationsOn filter by notifications on
   * @return list of searchHounds
   */
  fun list(userId: UUID?, categoryId: UUID?, notificationsOn: Boolean?): Array<SearchHound> {
    return api.listSearchHounds(userId, categoryId, notificationsOn)
  }

  /**
   * Creates new search hound with default values
   *
   * @param categoryId category id
   * @param userId user id
   * @return created searchHound
   */
  fun create(categoryId: UUID, userId: UUID): SearchHound {
    val searchHound = SearchHound(
      name = "Default name",
      notificationsOn = true,
      categoryId = categoryId,
      userId = userId,
      expires = OffsetDateTime.now().toString()
    )
    return create(searchHound)
  }

  /**
   * Create new searchHound with custom values
   *
   * @param searchHound searchHound with custom values
   * @return created SearchHound
   */
  fun create(searchHound: SearchHound): SearchHound {
    val createdSearchHound = api.createSearchHound(searchHound)
    addClosable(createdSearchHound)
    return createdSearchHound
  }

  /**
   * Find search hound with id
   *
   * @param searchHoundId id of the search hound
   * @return found search hound
   */
  fun findSearchHound(searchHoundId: UUID): SearchHound {
    return api.findSearchHound(searchHoundId)
  }

  /**
   * Update search hound
   *
   * @param searchHoundId search hound id
   * @param payload search hound to update
   * @return updated search hound
   */
  fun updateSearchHound(searchHoundId: UUID, payload: SearchHound): SearchHound? {
    return api.updateSearchHound(searchHoundId, payload)
  }

  /**
   * Deletes a search hound from the API and removes closable
   *
   * @param searchHoundId search hound id to be deleted
   */
  fun delete(searchHoundId: UUID) {
    api.deleteSearchHound(searchHoundId)
    removeCloseable{ closable: Any ->
      if (closable !is SearchHound) {
        return@removeCloseable false
      }

      val closeableSearchHound: SearchHound = closable
      closeableSearchHound.id!! == searchHoundId
    }
  }

  /**
   * Find that should fail with given status
   *
   * @param expectedStatus expected status code from server
   * @param searchHoundId search hound id
   */
  fun assertFindFail(expectedStatus: Int, searchHoundId: UUID) {
    try {
      api.findSearchHound(searchHoundId)
      Assert.fail(String.format("Expected find to fail with message %d", expectedStatus))
    } catch (e: ClientException) {
      assertClientExceptionStatus(expectedStatus, e)
    }
  }

  /**
   * Clean resources
   *
   * @param searchHound search hound to be cleaned
   */
  override fun clean(searchHound: SearchHound) {
    this.api.deleteSearchHound(searchHound.id!!)
  }
}