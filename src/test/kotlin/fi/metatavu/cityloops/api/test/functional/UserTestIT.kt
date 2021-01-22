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
      assertEquals(userToCreate.description, createdUser.description)
      assertEquals(userToCreate.logoUrl, createdUser.logoUrl)
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
  fun testFindPublicUser() {
    TestBuilder().use {
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
      val foundUser = it.admin().users().findPublicUser(createdUser.id!!)

      assertNotNull(foundUser)
      assertEquals(userToCreate.name, foundUser.name)
      assertEquals(userToCreate.address, foundUser.address)
      assertEquals(userToCreate.email, foundUser.email)
      assertEquals(userToCreate.phoneNumber, foundUser.phoneNumber)
      assertEquals(userToCreate.companyId, foundUser.companyId)
      assertEquals(userToCreate.coordinates, foundUser.coordinates)
      assertEquals(userToCreate.officeInfo, foundUser.officeInfo)
      assertEquals(userToCreate.description, foundUser.description)
      assertEquals(userToCreate.logoUrl, foundUser.logoUrl)
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
        coordinates = Coordinates(1.0, 1.0),
        description = "Updated description",
        logoUrl = "https://logourl.test"
      )

      val updatedUser = it.admin().users().updateUser(
        userId = defaultUser.id!!,
        payload = userToUpdate
      )

      assertEquals(userToUpdate.name, updatedUser.name)
      assertEquals(userToUpdate.address, updatedUser.address)
      assertEquals(userToUpdate.email, updatedUser.email)
      assertEquals(userToUpdate.phoneNumber, updatedUser.phoneNumber)
      assertEquals(userToUpdate.companyAccount, updatedUser.companyAccount)
      assertEquals(userToUpdate.verified, updatedUser.verified)
      assertEquals(userToUpdate.companyId, updatedUser.companyId)
      assertEquals(userToUpdate.officeInfo, updatedUser.officeInfo)
      assertEquals(userToUpdate.coordinates, updatedUser.coordinates)
      assertEquals(userToUpdate.description, updatedUser.description)
      assertEquals(userToUpdate.logoUrl, updatedUser.logoUrl)
    }
  }

}