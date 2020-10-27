package fi.metatavu.cityloops.api.test.functional

import fi.metatavu.cityloops.api.client.models.Category
import fi.metatavu.cityloops.api.client.models.CategoryInputType
import fi.metatavu.cityloops.api.client.models.CategoryProperty
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

      val subCategoryToCreate = Category(
        name = "Custom sub category",
        parentCategoryId = newCustomCategory.id
      )
      val createdSubCategory = it.admin().categories().create(subCategoryToCreate)

      assertNotNull(createdSubCategory)
      assertEquals(subCategoryToCreate.parentCategoryId, createdSubCategory.parentCategoryId)
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
      val emptyList = it.admin().categories().list(null)
      assertEquals(0, emptyList.size)

      val emptyListWithAnonymous = it.anonymousUser().categories().list(null)
      assertEquals(0, emptyListWithAnonymous.size)

      val firstCategoryToCreate = Category(
        name = "Category 1"
      )

      val secondCategoryToCreate = Category(
        name = "Category 2"
      )
      val category1 = it.admin().categories().create(firstCategoryToCreate)
      val category2 = it.admin().categories().create(secondCategoryToCreate)

      val categoryList = it.admin().categories().list(null)
      assertEquals(2, categoryList.size)

      val firstSubCategoryList = it.admin().categories().list(category1.id)
      assertEquals(0, firstSubCategoryList.size)

      val secondSubCategoryList = it.admin().categories().list(category2.id)
      assertEquals(0, secondSubCategoryList.size)

      val firstSubCategoryToCreate = Category(
        name = "Sub category 1",
        parentCategoryId = category1.id
      )
      val secondSubCategoryToCreate = Category(
        name = "Sub category 2",
        parentCategoryId = category1.id
      )

      it.admin().categories().create(firstSubCategoryToCreate)
      it.admin().categories().create(secondSubCategoryToCreate)

      val secondAllCategoryList = it.admin().categories().list(null)
      assertEquals(4, secondAllCategoryList.size)

      val secondAllCategoryListWithAnonymous = it.anonymousUser().categories().list(null)
      assertEquals(4, secondAllCategoryListWithAnonymous.size)

      val thirdSubCategoryList = it.admin().categories().list(category1.id)
      assertEquals(2, thirdSubCategoryList.size)
    }
  }

  @Test
  fun testUpdateCategory() {
    TestBuilder().use {
      val newDefaultCategory = it.admin().categories().create()
      val categoryId = newDefaultCategory.id!!

      val categoryToUpdate = Category (
        id = categoryId,
        name = "Updated category name",
        properties = arrayOf(
          CategoryProperty(
            name = "Property name",
            type = CategoryInputType.tEXT,
            required = false,
            defaultValue = "Default value",
            infoText = "Additional info of this property",
            unit = "cm"
          )
        )
      )

      val updatedCategory = it.admin().categories().updateCategory(categoryId = categoryId, payload = categoryToUpdate)
      assertNotNull(updatedCategory)
      assertEquals(categoryId, updatedCategory?.id)
      assertEquals(categoryToUpdate.name, updatedCategory?.name)
      assertEquals(categoryToUpdate.properties?.get(0)?.name, updatedCategory?.properties?.get(0)?.name)
      assertEquals(categoryToUpdate.properties?.get(0)?.type, updatedCategory?.properties?.get(0)?.type)
      assertEquals(categoryToUpdate.properties?.get(0)?.required, updatedCategory?.properties?.get(0)?.required)
      assertEquals(categoryToUpdate.properties?.get(0)?.defaultValue, updatedCategory?.properties?.get(0)?.defaultValue)
      assertEquals(categoryToUpdate.properties?.get(0)?.infoText, updatedCategory?.properties?.get(0)?.infoText)
      assertEquals(categoryToUpdate.properties?.get(0)?.unit, updatedCategory?.properties?.get(0)?.unit)
    }
  }

  @Test
  fun testDeleteCategory() {
    TestBuilder().use {
      val emptyList = it.admin().categories().list(null)
      assertEquals(0, emptyList.size)
      val firstCategoryId = it.admin().categories().create().id!!
      val secondCategoryId = it.admin().categories().create().id!!

      val categoryList = it.admin().categories().list(null)
      assertEquals(2, categoryList.size)

      it.admin().categories().delete(categoryId = firstCategoryId)
      val categoryListAfterFirstDelete = it.admin().categories().list(null)
      assertEquals(1, categoryListAfterFirstDelete.size)

      it.admin().categories().delete(categoryId = secondCategoryId)
      val categoryListAfterSecondDelete = it.admin().categories().list(null)
      assertEquals(0, categoryListAfterSecondDelete.size)

    }
  }

  @Test
  fun testUpdateCategoryOrder() {
    TestBuilder().use {

      val firstCategoryToCreate = Category(
        name = "Category 1"
      )
      val category1 = it.admin().categories().create(firstCategoryToCreate)
      val firstCategoryId = category1.id!!

      val secondCategoryToCreate = Category(
        name = "Category 2",
        parentCategoryId = firstCategoryId
      )
      val category2 = it.admin().categories().create(secondCategoryToCreate)
      val secondCategoryId = category2.id!!

      val thirdCategoryToCreate = Category(
        name = "Category 3",
        parentCategoryId = category2.id!!
      )
      val category3 = it.admin().categories().create(thirdCategoryToCreate)
      val thirdCategoryId = category3.id!!

      val fourthCategoryToCreate = Category(
        name = "Category 4",
        parentCategoryId = category3.id!!
      )
      val category4 = it.admin().categories().create(fourthCategoryToCreate)
      val fourthCategoryId = category4.id!!

      /**
       * Order should be 1 -> 2 -> 3 -> 4
       */
      assertEquals(null, category1.parentCategoryId)
      assertEquals(firstCategoryId, category2.parentCategoryId)
      assertEquals(secondCategoryId, category3.parentCategoryId)
      assertEquals(thirdCategoryId, category4.parentCategoryId)

      /**
       * Update orders
       */
      val firstCategoryToMove = Category(
        id = thirdCategoryId,
        name = category3.name,
        parentCategoryId = firstCategoryId
      )

      val secondCategoryToMove = Category(
        id = secondCategoryId,
        name = category2.name,
        parentCategoryId = thirdCategoryId
      )

      val thirdCategoryToMove = Category(
        id = fourthCategoryId,
        name = category4.name,
        parentCategoryId = secondCategoryId
      )

      it.admin().categories().updateCategory(
        categoryId = thirdCategoryId,
        payload = firstCategoryToMove
      )

      it.admin().categories().updateCategory(
        categoryId = secondCategoryId,
        payload = secondCategoryToMove
      )

      it.admin().categories().updateCategory(
        categoryId = fourthCategoryId,
        payload = thirdCategoryToMove
      )

      val orderedCategory1 = it.admin().categories().findCategory(firstCategoryId)
      val orderedCategory2 = it.admin().categories().findCategory(secondCategoryId)
      val orderedCategory3 = it.admin().categories().findCategory(thirdCategoryId)
      val orderedCategory4 = it.admin().categories().findCategory(fourthCategoryId)

      /**
       * Order should be 1 -> 3 -> 2 -> 4
       */
      assertEquals(null, orderedCategory1.parentCategoryId)
      assertEquals(firstCategoryId, orderedCategory3.parentCategoryId)
      assertEquals(thirdCategoryId, orderedCategory2.parentCategoryId)
      assertEquals(secondCategoryId, orderedCategory4.parentCategoryId)

    }
  }

}