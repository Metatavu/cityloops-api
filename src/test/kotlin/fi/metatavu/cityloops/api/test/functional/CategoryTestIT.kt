package fi.metatavu.cityloops.api.test.functional

import fi.metatavu.cityloops.api.client.models.Category
import org.junit.Test
import org.junit.Assert.*
import java.util.*

/**
 * JUnit tests for Category
 */
class CategoryTestIT: AbstractFunctionalTest() {

  @Test
  fun testCreateCategory() {
    TestBuilder().use {
      val newDefaultCategory = it.admin().categories().create()

      assertNotNull(newDefaultCategory)
      assertEquals("Default name", newDefaultCategory.name)
      val categoryToCreate = Category(
        name = "Custom category"
      )
      val newCustomCategory = it.admin().categories().create(category = categoryToCreate)
      assertNotNull(newCustomCategory)
      assertEquals("Custom category", newCustomCategory.name)
    }
  }

  @Test
  fun testFindCategory() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!

      val foundCategory = it.admin().categories().findCategory(categoryId)
      assertNotNull(foundCategory)
    }
  }

  @Test
  fun testListCategories() {
    TestBuilder().use {
      val emptyList = it.admin().categories().listAll()
      assertEquals(0, emptyList.size)
      it.admin().categories().create()
      it.admin().categories().create()

      val categoryList = it.admin().categories().listAll()
      assertEquals(2, categoryList.size)

    }
  }

  @Test
  fun testUpdateCategory() {
    TestBuilder().use {
      val newDefaultCategory = it.admin().categories().create()
      val categoryId = newDefaultCategory.id!!

      val categoryToUpdate = Category (
        id = categoryId,
        name = "Updated category name"
      )

      val updatedCategory = it.admin().categories().updateCategory(categoryId = categoryId, payload = categoryToUpdate)
      assertNotNull(updatedCategory)
      assertEquals(categoryId, updatedCategory?.id)
      assertEquals(categoryToUpdate.name, updatedCategory?.name)
    }
  }

  @Test
  fun testDeleteCategory() {
    TestBuilder().use {
      val emptyList = it.admin().categories().listAll()
      assertEquals(0, emptyList.size)
      val firstCategoryId = it.admin().categories().create().id!!
      val secondCategoryId = it.admin().categories().create().id!!

      val categoryList = it.admin().categories().listAll()
      assertEquals(2, categoryList.size)

      it.admin().categories().delete(categoryId = firstCategoryId)
      val categoryListAfterFirstDelete = it.admin().categories().listAll()
      assertEquals(1, categoryListAfterFirstDelete.size)

      it.admin().categories().delete(categoryId = secondCategoryId)
      val categoryListAfterSecondDelete = it.admin().categories().listAll()
      assertEquals(0, categoryListAfterSecondDelete.size)

    }
  }

}