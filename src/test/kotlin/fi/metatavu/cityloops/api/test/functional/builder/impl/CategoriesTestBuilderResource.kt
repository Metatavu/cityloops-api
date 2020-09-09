package fi.metatavu.cityloops.api.test.functional.builder.impl

import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.client.apis.CategoriesApi
import fi.metatavu.cityloops.api.client.infrastructure.ClientException
import fi.metatavu.cityloops.api.client.models.Category
import fi.metatavu.cityloops.api.test.functional.settings.TestSettings
import fi.metatavu.cityloops.api.test.functional.impl.ApiTestBuilderResource
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import org.junit.Assert
import java.util.*

class CategoriesTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<Category, ApiClient>(testBuilder, apiClient) {
  override fun getApi(): CategoriesApi {
    ApiClient.accessToken = accessTokenProvider?.accessToken
    return CategoriesApi(TestSettings.apiBasePath)
  }

  /**
   * List all categories
   *
   * @return list of categories
   */
  fun listAll(): Array<Category> {
    return api.listCategories()
  }
  /**
   * Creates new category with default values
   *
   * @return created category
   */
  fun create(): Category {
    val category = Category(name = "Default name")
    return create(category)
  }

  /**
   * Create new category with custom values
   *
   * @param category category with custom values
   * @return created Category
   */
  fun create(category: Category): Category {
    val createdCategory = api.createCategory(category)
    addClosable(createdCategory)
    return createdCategory
  }

  /**
   * Find category with id
   *
   * @param categoryId id of the category
   * @return found category
   */
  fun findCategory(categoryId: UUID): Category {
    return api.findCategory(categoryId)
  }

  /**
   * Update category
   *
   * @param payload category to update
   * @return updated category
   */
  fun updateCategory(categoryId: UUID, payload: Category): Category? {
    return api.updateCategory(categoryId, payload)
  }

  /**
   * Deletes a category from the API and removes closable
   *
   * @param categoryId category id to be deleted
   */
  fun delete(categoryId: UUID) {
    api.deleteCategory(categoryId)
    removeCloseable{ closable: Any ->
      if (closable !is Category) {
        return@removeCloseable false
      }

      val closeableCategory: Category = closable
      closeableCategory.id!!.equals(categoryId)
    }
  }

  /**
   * Find that should fail with given status
   *
   * @param expectedStatus expected status code from server
   * @param categoryId category id
   */
  fun assertFindFail(expectedStatus: Int, categoryId: UUID) {
    try {
      api.findCategory(categoryId)
      Assert.fail(String.format("Expected find to fail with message %d", expectedStatus))
    } catch (e: ClientException) {
      assertClientExceptionStatus(expectedStatus, e)
    }
  }

  /**
   * Clean resources
   *
   * @param category category to be cleaner
   */
  override fun clean(category: Category) {
    this.api.deleteCategory(category.id!!)
  }
}