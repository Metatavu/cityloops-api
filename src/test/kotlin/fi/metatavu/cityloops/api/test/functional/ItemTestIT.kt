package fi.metatavu.cityloops.api.test.functional

import fi.metatavu.cityloops.api.client.models.Item
import fi.metatavu.cityloops.api.client.models.ItemType
import fi.metatavu.cityloops.api.client.models.LocationInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

/**
 * JUnit tests for Item
 */
class ItemTestIT: AbstractFunctionalTest() {

  @Test
  fun testCreateItem() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@email.com").id!!

      val item = it.admin().items().create(categoryId, userId)
      assertNotNull(item)
    }
  }

  @Test
  fun testFindItem() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      val itemId = it.admin().items().create(categoryId, userId).id!!

      val foundItem = it.admin().items().findItem(itemId)
      assertNotNull(foundItem)

      val customItemToCreate = Item(
        title = "Custom title",
        category = categoryId,
        metadata = fi.metatavu.cityloops.api.client.models.Metadata(
          locationInfo = LocationInfo()
        ),
        onlyForCompanies = true,
        userId = userId,
        price = "0.0",
        priceUnit = "€/kpl",
        paymentMethod = "Cash only",
        delivery = true,
        deliveryPrice = 20.0,
        expired = false,
        itemType = ItemType.bUY
      )

      val customItem = it.admin().items().create(customItemToCreate)
      val secondFoundItem = it.admin().items().findItem(customItem.id!!)
      assertJsonsEqual(customItem, secondFoundItem)
    }
  }

  @Test
  fun testListItems() {
    TestBuilder().use {
      val emptyListForAnonymous = it.anonymousUser().items().list(null, null, null, null, null, false, null)
      assertEquals(0, emptyListForAnonymous.size)
      val emptyList = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(0, emptyList.size)

      val userId = it.admin().users().create("email1@example.com").id!!
      val secondUserId = it.admin().users().create("email2@example.com").id!!
      val categoryId = it.admin().categories().create().id!!

      it.admin().items().create(categoryId, userId)
      val listWithOneItem = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(1, listWithOneItem.size)

      it.admin().items().create(categoryId, userId)
      val listWithTwoItems = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(2, listWithTwoItems.size)

      val itemWithSecondUserId = it.admin().items().create(categoryId, secondUserId)
      val listWithTreeItems = it.admin().items().list(null, null, null, null, null, false, null)
      val listWithTreeItemsAnonymous = it.anonymousUser().items().list(null, null, null, null, null, false, null)
      assertEquals(3, listWithTreeItems.size)
      assertEquals(3, listWithTreeItemsAnonymous.size)

      val firstItem = listWithTreeItems[0]
      val secondItem = listWithTreeItems[1]
      val thirdItem = listWithTreeItems[2]

      val idList = listWithTreeItems
        .map { it.id }

      assertEquals(false, idList.contains(UUID.randomUUID()))
      assertEquals(true, idList.contains(firstItem.id))
      assertEquals(true, idList.contains(secondItem.id))
      assertEquals(true, idList.contains(thirdItem.id))

      val listWithFirstResult = it.admin().items().list(null, null, 1, null, null, false, null)
      val listWithMaxResult = it.admin().items().list(null, null, null, 1, null, false, null)

      assertEquals(2, listWithFirstResult.size)
      assertEquals(1, listWithMaxResult.size)

      val categoryId2 = it.admin().categories().create().id!!
      it.admin().items().create(categoryId2, userId)
      val listByOldestFirst = it.admin().items().list(null, null, null, null, true, false, null)
      assertEquals(categoryId2, listByOldestFirst.last().category)

      val listByUser = it.admin().items().list(secondUserId, null, null, null, null, false, null)
      assertEquals(1, listByUser.size)
      assertEquals(itemWithSecondUserId.id!!, listByUser[0].id!!)

      val secondCategoryId = it.admin().categories().create().id!!
      it.admin().items().create(secondCategoryId, userId)
      val listByCategory = it.admin().items().list(null,  secondCategoryId, null, null, null, false, null)
      assertEquals(1, listByCategory.size)
    }
  }

  @Test
  fun testUpdateItem() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      val itemId = it.admin().items().create(categoryId, userId).id!!

      val itemToUpdate = Item(
        id = itemId,
        title = "Updated title",
        category = categoryId,
        metadata = fi.metatavu.cityloops.api.client.models.Metadata(
          locationInfo = LocationInfo(
            email = "example@email.com",
            address = "Testaddress 1",
            description = "Custom item description",
            phone = "123456789456"
          ),
          amount = 10
        ),
        onlyForCompanies = true,
        userId = userId,
        price = "100.0",
        priceUnit = "£/kpl",
        paymentMethod = "Cash & Credit card",
        delivery = true,
        deliveryPrice = 20.0,
        expired = false,
        itemType = ItemType.rENT
      )

      val updatedItem = it.admin().items().updateItem(
        itemId = itemId,
        payload = itemToUpdate
      )

      assertEquals(itemToUpdate.title, updatedItem.title)
      assertEquals(itemToUpdate.category, updatedItem.category)
      assertEquals(itemToUpdate.onlyForCompanies, updatedItem.onlyForCompanies)
      assertEquals(itemToUpdate.metadata.amount, updatedItem.metadata.amount)
      assertEquals(itemToUpdate.metadata.locationInfo.email, updatedItem.metadata.locationInfo.email)
      assertEquals(itemToUpdate.metadata.locationInfo.address, updatedItem.metadata.locationInfo.address)
      assertEquals(itemToUpdate.metadata.locationInfo.description, updatedItem.metadata.locationInfo.description)
      assertEquals(itemToUpdate.metadata.locationInfo.phone, updatedItem.metadata.locationInfo.phone)
      assertEquals(itemToUpdate.price, updatedItem.price)
      assertEquals(itemToUpdate.priceUnit, updatedItem.priceUnit)
      assertEquals(itemToUpdate.paymentMethod, updatedItem.paymentMethod)
      assertEquals(itemToUpdate.delivery, updatedItem.delivery)
      assertEquals(itemToUpdate.deliveryPrice, updatedItem.deliveryPrice)
      assertEquals(itemToUpdate.itemType, updatedItem.itemType)
    }
  }

  @Test
  fun testDeleteItem() {
    TestBuilder().use {
      val firstList = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(0, firstList.size)

      val userId = it.admin().users().create("email@example.com").id!!
      val categoryId = it.admin().categories().create().id!!
      val firstId = it.admin().items().create(categoryId, userId).id!!

      val secondList = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(1, secondList.size)

      val secondId = it.admin().items().create(categoryId, userId).id!!
      val thirdId = it.admin().items().create(categoryId, userId).id!!

      val thirdList = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(3, thirdList.size)

      it.admin().items().delete(firstId)
      val listAfterFirstDelete = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(2, listAfterFirstDelete.size)

      it.admin().items().delete(secondId)
      it.admin().items().delete(thirdId)
      val listAfterSecondDelete = it.admin().items().list(null, null, null, null, null, false, null)
      assertEquals(0, listAfterSecondDelete.size)
    }
  }

  @Test
  fun testAddItemImages() {
    TestBuilder().use {
      val userId = it.admin().users().create("email@example.com").id!!
      val categoryId = it.admin().categories().create().id!!

      val customItemToCreate = Item(
        title = "Custom title",
        category = categoryId,
        metadata = fi.metatavu.cityloops.api.client.models.Metadata(
          LocationInfo()
        ),
        onlyForCompanies = true,
        userId = userId,
        images = arrayOf("http://example.com/image1.png", "http://example.com/image2.png"),
        price = "0.0",
        priceUnit = "€/kpl",
        paymentMethod = "Cash only",
        delivery = false,
        expired = false,
        itemType = ItemType.bUY
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
        userId = userId,
        images = arrayOf(
          "http://example.com/image2.png",
          "http://example.com/image3.png",
          "http://example.com/image4.png"
        ),
        price = "0.0",
        priceUnit = "€/kpl",
        paymentMethod = "Cash only",
        delivery = false,
        expired = false,
        itemType = ItemType.bUY
      )

      val updatedItem = it.admin().items().updateItem(itemId = createdItem.id!!, payload = customItemToUpdate)
      assertNotNull(updatedItem)

      assertEquals(customItemToUpdate.images?.size, updatedItem.images?.size)
      assertEquals(false, updatedItem.images?.contains("http://example.com/image1.png"))
      assertEquals(true, updatedItem.images?.contains("http://example.com/image3.png"))
    }
  }

}