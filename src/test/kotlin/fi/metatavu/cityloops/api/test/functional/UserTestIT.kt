package fi.metatavu.cityloops.api.test.functional

import fi.metatavu.cityloops.api.client.models.Coordinates
import fi.metatavu.cityloops.api.client.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

/**
 * JUnit tests for User
 */
class UserTestIT: AbstractFunctionalTest() {

  @Test
  fun testCreateUser() {
    TestBuilder().use {
      val defaultUser = it.admin().users().create("email@example.com")
      assertNotNull(defaultUser)

      val defaultUserWithAnonymous = it.anonymousUser().users().create("email1@example.com")
      assertNotNull(defaultUserWithAnonymous)
      it.admin().users().delete(defaultUserWithAnonymous.id!!)

      val userToCreate = User(
        name = "Custom name",
        address = "Custom address",
        email = "custom@email.com",
        phoneNumber = "9876543210",
        companyAccount = true,
        verified = false,
        password = "custom_password",
        companyId = "123123123-A",
        officeInfo = "Some office info"
      )

      val createdUser = it.admin().users().create(userToCreate)
      assertEquals(userToCreate.name, createdUser.name)
      assertEquals(userToCreate.address, createdUser.address)
      assertEquals(userToCreate.email, createdUser.email)
      assertEquals(userToCreate.phoneNumber, createdUser.phoneNumber)
      assertEquals(userToCreate.companyAccount, createdUser.companyAccount)
      assertEquals(userToCreate.verified, createdUser.verified)
      assertEquals(userToCreate.companyId, createdUser.companyId)
      assertEquals(userToCreate.officeInfo, createdUser.officeInfo)
    }
  }

  @Test
  fun testFindUser() {
    TestBuilder().use {
      val defaultUser = it.admin().users().create("email@example.com")
      val foundUser = it.admin().users().findUser(defaultUser.id!!)

      assertNotNull(foundUser)
      assertJsonsEqual(defaultUser, foundUser)
    }
  }

  @Test
  fun testListUsers() {
    TestBuilder().use {
      val emptyList = it.admin().users().list(null, null)
      assertEquals(0, emptyList.size)

      it.admin().users().create("email1@example.com")
      it.admin().users().create("email2@example.com")

      val listWithTwoUsers = it.admin().users().list(null, null)
      assertEquals(2, listWithTwoUsers.size)

      val userToCreate = User(
        name = "Custom name",
        address = "Custom address",
        email = "custom@email.com",
        phoneNumber = "9876543210",
        companyAccount = true,
        verified = true,
        password = "custom_password"
      )

      val createdUser = it.admin().users().create(userToCreate)

      val listWithThreeUsers = it.admin().users().list(null, null)
      assertEquals(3, listWithThreeUsers.size)

      val firstFoundUser = listWithThreeUsers[0]
      val secondFoundUser = listWithThreeUsers[1]
      val thirdFoundUser = listWithThreeUsers[2]

      val idList = listWithThreeUsers
        .map { it.id }

      assertEquals(false, idList.contains(UUID.randomUUID()))
      assertEquals(true, idList.contains(firstFoundUser.id))
      assertEquals(true, idList.contains(secondFoundUser.id))
      assertEquals(true, idList.contains(thirdFoundUser.id))

      val listByCompanyUsers = it.admin().users().list(true, null)
      assertEquals(1, listByCompanyUsers.size)
      assertEquals(createdUser.id, listByCompanyUsers[0].id)

      val listByVerifiedUsers = it.admin().users().list(null, true)
      assertEquals(1, listByVerifiedUsers.size)
      assertEquals(createdUser.id, listByVerifiedUsers[0].id)

      val listByCompanyUserAndVerifiedUsers = it.admin().users().list(true, true)
      assertEquals(1, listByCompanyUserAndVerifiedUsers.size)
      assertEquals(createdUser.id, listByCompanyUserAndVerifiedUsers[0].id)

      val listByVerifiedPrivateUsers = it.admin().users().list(false, true)
      assertEquals(0, listByVerifiedPrivateUsers.size)

    }
  }

  @Test
  fun testUpdateUser() {
    TestBuilder().use {
      val defaultUser = it.admin().users().create("email@example.com")
      assertNotNull(defaultUser)

      val userToUpdate = User(
        id = defaultUser.id!!,
        name = "Custom name",
        address = "Custom address",
        email = "custom@email.com",
        phoneNumber = "9876543210",
        companyAccount = true,
        verified = false,
        companyId = "123123123-A",
        officeInfo = "Some office info",
        coordinates = Coordinates(1.0, 1.0)
      )

      val updatedUser = it.admin().users().updateUser(
        userId = defaultUser.id!!,
        payload = userToUpdate
      )

      assertEquals(userToUpdate.name, updatedUser?.name)
      assertEquals(userToUpdate.address, updatedUser?.address)
      assertEquals(userToUpdate.email, updatedUser?.email)
      assertEquals(userToUpdate.phoneNumber, updatedUser?.phoneNumber)
      assertEquals(userToUpdate.companyAccount, updatedUser?.companyAccount)
      assertEquals(userToUpdate.verified, updatedUser?.verified)
      assertEquals(userToUpdate.companyId, updatedUser?.companyId)
      assertEquals(userToUpdate.officeInfo, updatedUser?.officeInfo)
      assertEquals(userToUpdate.coordinates, updatedUser?.coordinates)
    }
  }

  @Test
  fun testDeleteUser() {
    TestBuilder().use {
      val emptyList = it.admin().users().list(null, null)
      assertEquals(0, emptyList.size)

      val firstUser = it.admin().users().create("email1@example.com")
      val secondUser = it.admin().users().create("email2@example.com")
      val listWithTwoUsers = it.admin().users().list(null, null)
      assertEquals(2, listWithTwoUsers.size)

      it.admin().users().delete(userId = firstUser.id!!)
      val listWithOneUsers = it.admin().users().list(null, null)
      assertEquals(1, listWithOneUsers.size)

      it.admin().users().delete(userId = secondUser.id!!)
      val listWithoutUsers = it.admin().users().list(null, null)
      assertEquals(0, listWithoutUsers.size)
    }
  }

}