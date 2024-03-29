package fi.metatavu.cityloops.api.test.functional

import java.io.IOException
import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.test.functional.auth.TestBuilderAuthentication
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication

import fi.metatavu.cityloops.api.test.functional.settings.TestSettings

import org.slf4j.LoggerFactory

/**
 * TestBuilder implementation
 *
 * @author Jari Nykänen
 */
class TestBuilder: AbstractTestBuilder<ApiClient> () {

  private val logger = LoggerFactory.getLogger(javaClass)

  private var admin: TestBuilderAuthentication? = null

  private var anonymous: TestBuilderAuthentication? = null

  override fun createTestBuilderAuthentication(testBuilder: AbstractTestBuilder<ApiClient>, accessTokenProvider: AccessTokenProvider): AuthorizedTestBuilderAuthentication<ApiClient> {
    return TestBuilderAuthentication(testBuilder, accessTokenProvider)
  }

  /**
   * Returns admin authenticated authentication resource
   *
   * @return admin authenticated authentication resource
   * @throws IOException
   */
  @kotlin.jvm.Throws(IOException::class)
  fun admin(): TestBuilderAuthentication {
    if (admin == null) {
      val authServerUrl = TestSettings.keycloakHost
      val realm = TestSettings.keycloakRealm
      val clientId = TestSettings.keycloakClientId
      val adminUser = TestSettings.keycloakAdminUser
      val adminPassword = TestSettings.keycloakAdminPass
      val clientSecret = TestSettings.keycloakClientSecret

      admin = TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, adminUser, adminPassword, clientSecret))
    }

    return admin!!
  }

  /**
   * Returns anonymous authenticated authentication resource
   *
   * @return anonymous authenticated authentication resource
   * @throws IOException
   */
  @kotlin.jvm.Throws(IOException::class)
  fun anonymousUser(): TestBuilderAuthentication {
    if (anonymous == null) {
      val authServerUrl = TestSettings.keycloakHost
      val realm = TestSettings.keycloakRealm
      val clientId = TestSettings.keycloakClientId
      val anonymousUser = TestSettings.keycloakAnonymousUser
      val anonymousPassword = TestSettings.keycloakAnonymousPass
      val clientSecret = TestSettings.keycloakClientSecret

      anonymous = TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, anonymousUser, anonymousPassword, clientSecret))
    }

    return anonymous!!
  }
}