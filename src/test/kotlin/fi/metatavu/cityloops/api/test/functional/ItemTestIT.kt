package fi.metatavu.cityloops.api.test.functional

import fi.metatavu.cityloops.api.client.models.Condition
import fi.metatavu.cityloops.api.client.models.Item
import fi.metatavu.cityloops.api.spec.model.Metadata
import org.junit.Test
import org.junit.Assert.*
import java.util.*

/**
 * JUnit tests for Item
 */
class ItemTestIT: AbstractFunctionalTest() {

  @Test
  fun testCreateItem() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      assertNotNull(categoryId)
      val item = it.admin().items().create(categoryId)
      assertNotNull(item)
    }
  }

  @Test
  fun testFindItem() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val itemId = it.admin().items().create(categoryId).id!!

      val foundItem = it.admin().items().findItem(itemId)
      assertNotNull(foundItem)

      val customItemToCreate = Item(
        title = "Custom title",
        category = categoryId,
        metadata = fi.metatavu.cityloops.api.client.models.Metadata(),
        onlyForCompanies = true
      )

      val customItem = it.admin().items().create(customItemToCreate)
      assertEquals(customItemToCreate.title, customItem.title)
      assertEquals(customItemToCreate.category, customItem.category)
      assertEquals(customItemToCreate.onlyForCompanies, customItem.onlyForCompanies)
    }
  }

  @Test
  fun testListItems() {
    TestBuilder().use {
      val firstList = it.admin().items().list(null)
      assertEquals(0, firstList.size)

      val categoryId = it.admin().categories().create().id!!
      it.admin().items().create(categoryId)

      val secondList = it.admin().items().list(null)
      assertEquals(1, secondList.size)

      it.admin().items().create(categoryId)
      it.admin().items().create(categoryId)

      val thirdList = it.admin().items().list(null)
      assertEquals(3, thirdList.size)
    }
  }

  @Test
  fun testUpdateItem() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val itemId = it.admin().items().create(categoryId).id!!

      val itemToUpdate = Item(
        id = itemId,
        title = "Updated title",
        category = categoryId,
        metadata = fi.metatavu.cityloops.api.client.models.Metadata(
          amount = 10,
          additionalInfo = "Information",
          condition = Condition.gOOD,
          price = 10.00,
          priceUnit = "€"
        ),
        onlyForCompanies = true
      )

      val updatedItem = it.admin().items().updateItem(
        itemId = itemId,
        payload = itemToUpdate
      )

      assertEquals(itemToUpdate.title, updatedItem?.title)
      assertEquals(itemToUpdate.category, updatedItem?.category)
      assertEquals(itemToUpdate.onlyForCompanies, updatedItem?.onlyForCompanies)
      assertEquals(itemToUpdate.metadata.amount, updatedItem?.metadata?.amount)
      assertEquals(itemToUpdate.metadata.additionalInfo, updatedItem?.metadata?.additionalInfo)
      assertEquals(itemToUpdate.metadata.condition, updatedItem?.metadata?.condition)
      assertEquals(itemToUpdate.metadata.price, updatedItem?.metadata?.price)
      assertEquals(itemToUpdate.metadata.priceUnit, updatedItem?.metadata?.priceUnit)
    }
  }

  @Test
  fun testDeleteItem() {
    TestBuilder().use {
      val firstList = it.admin().items().list(null)
      assertEquals(0, firstList.size)

      val categoryId = it.admin().categories().create().id!!
      val firstId = it.admin().items().create(categoryId).id!!

      val secondList = it.admin().items().list(null)
      assertEquals(1, secondList.size)

      val secondId = it.admin().items().create(categoryId).id!!
      val thirdId = it.admin().items().create(categoryId).id!!

      val thirdList = it.admin().items().list(null)
      assertEquals(3, thirdList.size)

      it.admin().items().delete(firstId)
      val listAfterFirstDelete = it.admin().items().list(null)
      assertEquals(2, listAfterFirstDelete.size)

      it.admin().items().delete(secondId)
      it.admin().items().delete(thirdId)
      val listAfterSecondDelete = it.admin().items().list(null)
      assertEquals(0, listAfterSecondDelete.size)
    }
  }

  @Test
  fun testAddItemImages() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!

      val customItemToCreate = Item(
        title = "Custom title",
        category = categoryId,
        metadata = fi.metatavu.cityloops.api.client.models.Metadata(),
        onlyForCompanies = true,
        images = arrayOf("http://example.com/image1.png", "http://example.com/image2.png")
      )

      val createdItem = it.admin().items().create(customItemToCreate)
      assertNotNull(createdItem)

      assertEquals(customItemToCreate.images?.size, createdItem.images?.size)
      assertEquals(true, createdItem.images?.contains("http://example.com/image1.png"))

      val customItemToUpdate = Item(
        id = createdItem.id,
        title = createdItem.title,
        category = createdItem.category,
        metadata = createdItem.metadata,
        onlyForCompanies = createdItem.onlyForCompanies,
        images = arrayOf(
          "http://example.com/image2.png",
          "http://example.com/image3.png",
          "http://example.com/image4.png"
        )
      )

      val updatedItem = it.admin().items().updateItem(itemId = createdItem.id!!, payload = customItemToUpdate)
      assertNotNull(updatedItem)

      assertEquals(customItemToUpdate.images?.size, updatedItem?.images?.size)
      assertEquals(false, updatedItem?.images?.contains("http://example.com/image1.png"))
      assertEquals(true, updatedItem?.images?.contains("http://example.com/image3.png"))
    }
  }

}