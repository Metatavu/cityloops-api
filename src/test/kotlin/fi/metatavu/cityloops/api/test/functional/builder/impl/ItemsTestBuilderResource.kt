package fi.metatavu.cityloops.api.test.functional.builder.impl

import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.client.apis.ItemsApi
import fi.metatavu.cityloops.api.client.infrastructure.ClientException
import fi.metatavu.cityloops.api.client.models.Item
import fi.metatavu.cityloops.api.client.models.LocationInfo
import fi.metatavu.cityloops.api.client.models.Metadata
import fi.metatavu.cityloops.api.spec.model.Category
import fi.metatavu.cityloops.api.test.functional.settings.TestSettings
import fi.metatavu.cityloops.api.test.functional.impl.ApiTestBuilderResource
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import org.junit.Assert
import java.util.*

class ItemsTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<Item, ApiClient>(testBuilder, apiClient) {
  override fun getApi(): ItemsApi {
    ApiClient.accessToken = accessTokenProvider?.accessToken
    return ItemsApi(TestSettings.apiBasePath)
  }

  /**
   * List items. Can be filtered by user
   *
   * @param userId user id
   * @param categoryId category id
   * @param firstResult index of the first result
   * @param maxResults limit amount of results to this number
   * @param returnOldestFirst return oldest first
   * @return list of items
   */
  fun list(userId: UUID?, categoryId: UUID?, firstResult: Int?, maxResults: Int?, returnOldestFirst: Boolean?): Array<Item> {
    return api.listItems(userId, categoryId, firstResult, maxResults, returnOldestFirst)
  }

  /**
   * Creates new item with default values
   *
   * @param categoryId category ID
   * @param userId user ID
   * @return created item
   */
  fun create(categoryId: UUID, userId: UUID): Item {
    val item = Item(
      title = "Default title",
      category = categoryId,
      onlyForCompanies = false,
      userId = userId,
      metadata = Metadata(
        locationInfo = LocationInfo()
      )
    )
    return create(item)
  }

  /**
   * Create new item with custom values
   *
   * @param item item with custom values
   * @return created Item
   */
  fun create(item: Item): Item {
    val createdItem = api.createItem(item)
    addClosable(createdItem)
    return createdItem
  }

  /**
   * Find item with id
   *
   * @param itemId id of the item
   * @return found item
   */
  fun findItem(itemId: UUID): Item {
    return api.findItem(itemId)
  }

  /**
   * Update item
   *
   * @param payload item to update
   * @return updated item
   */
  fun updateItem(itemId: UUID, payload: Item): Item? {
    return api.updateItem(itemId, payload)
  }

  /**
   * Deletes a item from the API and removes closable
   *
   * @param itemId item id to be deleted
   */
  fun delete(itemId: UUID) {
    api.deleteItem(itemId)
    removeCloseable{ closable: Any ->
      if (closable !is Item) {
        return@removeCloseable false
      }

      val closeableItem: Item = closable
      closeableItem.id!!.equals(itemId)
    }
  }

  /**
   * Find that should fail with given status
   *
   * @param expectedStatus expected status code from server
   * @param itemId item id
   */
  fun assertFindFail(expectedStatus: Int, itemId: UUID) {
    try {
      api.findItem(itemId)
      Assert.fail(String.format("Expected find to fail with message %d", expectedStatus))
    } catch (e: ClientException) {
      assertClientExceptionStatus(expectedStatus, e)
    }
  }

  /**
   * Clean resources
   *
   * @param item item to be cleaner
   */
  override fun clean(item: Item) {
    this.api.deleteItem(item.id!!)
  }
}
