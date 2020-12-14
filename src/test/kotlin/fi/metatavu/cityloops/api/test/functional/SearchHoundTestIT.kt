package fi.metatavu.cityloops.api.test.functional

import fi.metatavu.cityloops.api.client.models.SearchHound
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset


/**
 * JUnit tests for Search hound
 */
class SearchHoundTestIT: AbstractFunctionalTest() {

  @Test
  fun testCreateSearchHound() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      val searchHound = it.admin().searchHounds().create(categoryId, userId)
      assertNotNull(searchHound)

      val customSearchHoundToCreate = SearchHound(
        name = "Custom name",
        notificationsOn = true,
        categoryId = categoryId,
        userId = userId,
        expires = OffsetDateTime.now().toString(),
        maxPrice = 100.0,
        minPrice = 10.0
      )

      val customSearchHound = it.admin().searchHounds().create(customSearchHoundToCreate)
      assertNotNull(customSearchHound)
    }
  }

  @Test
  fun testFindSearchHound() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      val searchHound = it.admin().searchHounds().create(categoryId, userId)

      val foundSearchHound = it.admin().searchHounds().findSearchHound(searchHound.id!!)
      assertJsonsEqual(searchHound, foundSearchHound)
    }
  }

  @Test
  fun testListSearchHounds() {
    TestBuilder().use {
      assertEquals(0, it.admin().searchHounds().list(null, null, null).size)

      val categoryId = it.admin().categories().create().id!!
      val secondCategoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      it.admin().searchHounds().create(categoryId, userId)

      val listWithOne = it.admin().searchHounds().list(null, null, null)
      assertEquals(1, listWithOne.size)

      it.admin().searchHounds().create(categoryId, userId)
      it.admin().searchHounds().create(categoryId, userId)

      val listWithThree = it.admin().searchHounds().list(null, null, null)
      assertEquals(3, listWithThree.size)

      val secondUserId = it.admin().users().create("email2@example.com").id!!
      it.admin().searchHounds().create(secondCategoryId, secondUserId)

      val listForFirstUser = it.admin().searchHounds().list(userId, null, null)
      assertEquals(3, listForFirstUser.size)

      val listForSecondUser = it.admin().searchHounds().list(secondUserId, null, null)
      assertEquals(1, listForSecondUser.size)

      val listForSecondCategoryId = it.admin().searchHounds().list(null, secondCategoryId, null)
      assertEquals(1, listForSecondCategoryId.size)

      val emptyList = it.admin().searchHounds().list(userId, secondCategoryId, null)
      assertEquals(0, emptyList.size)
    }
  }

  @Test
  fun testUpdateSearchHound() {
    TestBuilder().use {
      val categoryId = it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      val searchHound = it.admin().searchHounds().create(categoryId, userId)
      assertNotNull(searchHound)

      val searchHoundToUpdate = SearchHound(
        id = searchHound.id!!,
        name = "Updated search hound name",
        notificationsOn = false,
        categoryId = searchHound.categoryId,
        userId = searchHound.userId,
        expires = OffsetDateTime.of(
          LocalDateTime.of(
            2021, 6, 1, 23, 59
          ),
          ZoneOffset.ofHoursMinutes(0, 0)
        ).toString(),
        maxPrice = 1000.0,
        minPrice = 100.0
      )

      val updatedSearchHound = it.admin().searchHounds().updateSearchHound(searchHound.id!!, searchHoundToUpdate)
      assertNotNull(updatedSearchHound)
      assertEquals(searchHoundToUpdate.name, updatedSearchHound?.name)
      assertEquals(searchHoundToUpdate.notificationsOn, updatedSearchHound?.notificationsOn)
      assertEquals(searchHoundToUpdate.categoryId, updatedSearchHound?.categoryId)
      assertEquals(searchHoundToUpdate.userId, updatedSearchHound?.userId)
      assertEquals(0,
        OffsetDateTime
          .parse(searchHoundToUpdate.expires)
          .compareTo(OffsetDateTime.parse(updatedSearchHound?.expires)
        ))
      assertEquals(searchHoundToUpdate.maxPrice, updatedSearchHound?.maxPrice)
      assertEquals(searchHoundToUpdate.minPrice, updatedSearchHound?.minPrice)

    }
  }

  @Test
  fun testDeleteSearchHound() {
    TestBuilder().use {
      assertEquals(0, it.admin().searchHounds().list(null, null, null).size)

      val categoryId = it.admin().categories().create().id!!
      it.admin().categories().create().id!!
      val userId = it.admin().users().create("email@example.com").id!!
      val searchHound = it.admin().searchHounds().create(categoryId, userId)

      val listWithOne = it.admin().searchHounds().list(null, null, null)
      assertEquals(1, listWithOne.size)

      it.admin().searchHounds().delete(searchHound.id!!)

      val listAfterDelete = it.admin().searchHounds().list(null, null, null)
      assertEquals(0, listAfterDelete.size)
    }
  }

}