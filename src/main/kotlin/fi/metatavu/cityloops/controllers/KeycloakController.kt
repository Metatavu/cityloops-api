package fi.metatavu.cityloops.controllers

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.Logger
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Controller for Keycloak related operations
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class KeycloakController {

  @Inject
  private lateinit var logger: Logger

  /**
   * Finds a Keycloak user by user id
   *
   * @param id user id
   * @return user or null if not found
   */
  fun findUserById(id: UUID?): UserRepresentation? {
    id ?: return null
    return keycloakClient.realm(realm).users().get(id.toString()).toRepresentation()
  }

  /**
   * Finds a Keycloak user by user email
   *
   * @param email user email
   * @return user or null if not found
   */
  fun findUserByEmail(email: String): UserRepresentation? {
    val users = searchUsers(
      username = null,
      firstName =  null,
      lastName = null,
      email = email,
      firstResult = 0,
      maxResults = 1
    )

    return users.firstOrNull()
  }

  /**
   * Creates new Keycloak user
   *
   * @param email user email
   * @param password user password
   * @return created user or null when creation has failed
   */
  fun createUser(email: String, password: String): UserRepresentation? {
    val usersResource = keycloakClient.realm(realm).users()
    val user = UserRepresentation()
    user.email = email
    user.username = email
    user.isEnabled = true

    val credentialRepresentation = CredentialRepresentation()
    credentialRepresentation.isTemporary = false
    credentialRepresentation.value = password
    credentialRepresentation.type = CredentialRepresentation.PASSWORD

    user.credentials = listOf(credentialRepresentation)

    try {
      val userId = getCreatedResponseId(usersResource.create(user))
      userId ?: return null
      return usersResource.get(userId.toString()).toRepresentation()
    } catch (e: javax.ws.rs.WebApplicationException) {
      if (logger.isErrorEnabled) {
        logger.error("Failed to create user: {}", IOUtils.toString(e.response.entity as InputStream, "UTF-8"))
      }
    }

    return null
  }

  /**
   * Delete user from keycloak
   *
   * @param id keycloak id
   */
  fun deleteUser(id: String) {
    keycloakClient.realm(realm).users().delete(id)
  }

  /**
   * Searches users from Keycloak
   *
   * @param username filter by Keycloak username
   * @param firstName filter by firstName
   * @param lastName filter by lastName
   * @param email filter by email
   * @param firstResult first result
   * @param maxResults max result amount
   */
  private fun searchUsers(
    username: String?,
    firstName: String?,
    lastName: String?,
    email: String?,
    firstResult: Int?,
    maxResults: Int?
  ): List<UserRepresentation> {
    try {
      return keycloakClient.realm(realm).users().search(
        username,
        firstName,
        lastName,
        email,
        firstResult,
        maxResults
      )
    } catch (e: javax.ws.rs.WebApplicationException) {
      if (logger.isErrorEnabled) {
        logger.error("Failed to search users: {}", IOUtils.toString(e.response.entity as InputStream, "UTF-8"))
      }
    }

    return listOf()
  }

  /**
   * Finds an id from Keycloak create response
   *
   * @param response response object
   * @return id
   */
  private fun getCreatedResponseId(response: Response): UUID? {
    if (response.status != 201) {
      try {
        if (logger.isErrorEnabled) {
          logger.error("Failed to execute create: {}", IOUtils.toString(response.entity as InputStream, "UTF-8"))
        }
      } catch (e: IOException) {
        logger.error("Failed to extract error message", e)
      }
      return null
    }

    val location: String = response.getHeaderString("location")
    if (StringUtils.isBlank(location)) {
      val objectMapper = ObjectMapper()
      try {
        val idExtract: IdExtract = objectMapper.readValue(response.entity as InputStream, IdExtract::class.java)
        return UUID.fromString(idExtract.id)
      } catch (e: IOException) {
        // Ignore JSON errors
      }

      return null
    }
    val pattern: Pattern = Pattern.compile(".*/(.*)$")
    val matcher: Matcher = pattern.matcher(location)
    return if (matcher.find()) {
      UUID.fromString(matcher.group(1))
    } else null
  }

  /**
   * Get initialized keycloak client
   */
  private val keycloakClient: Keycloak
    get() {
      return KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm(realm)
        .clientId(adminResource)
        .clientSecret(adminSecret)
        .grantType(OAuth2Constants.PASSWORD)
        .username(adminUser)
        .password(adminPassword)
        .build()
    }

  /**
   * Returns API admin access token
   */
  private val adminAccessToken: String?
    get() {
      val uri = "$serverUrl/realms/$realm/protocol/openid-connect/token"
      try {
        HttpClients.createDefault().use { client ->
          val httpPost = HttpPost(uri)
          val params: MutableList<NameValuePair> = ArrayList()
          params.add(BasicNameValuePair("client_id", adminResource))
          params.add(BasicNameValuePair("grant_type", "password"))
          params.add(BasicNameValuePair("username", adminUser))
          params.add(BasicNameValuePair("password", adminPassword))
          params.add(BasicNameValuePair("client_secret", adminSecret))
          httpPost.entity = UrlEncodedFormEntity(params)
          client.execute(httpPost).use { response ->
            if (response.statusLine.statusCode != 200) {
              logger.error("Failed obtain access token: {}", IOUtils.toString(response.entity.content, "UTF-8"))
              return null
            }

            response.entity.content.use { inputStream ->
              val responseMap: Map<String, Any> = readJsonMap(inputStream)
              return responseMap["access_token"] as String?
            }
          }
        }
      } catch (e: IOException) {
        logger.debug("Failed to retrieve access token", e)
      }
      return null
    }

  /**
   * Reads JSON src into Map
   *
   * @param src input
   * @return map
   * @throws IOException throws IOException when there is error when reading the input
   */
  @Throws(IOException::class)
  private fun readJsonMap(src: InputStream): Map<String, Any> {
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(src, object : TypeReference<Map<String, Any>>() {})
  }

  /**
   * Returns Keycloak client id
   */
  private val adminResource: String
    get() = System.getenv("KEYCLOAK_ADMIN_RESOURCE")

  /**
   * Returns Keycloak api secret
   */
  private val adminSecret: String
    get() = System.getenv("KEYCLOAK_ADMIN_SECRET")

  /**
   * Returns Keycloak admin password
   */
  private val adminPassword: String
    get() = System.getenv("KEYCLOAK_ADMIN_PASSWORD")

  /**
   * Returns Keycloak admin username
   */
  private val adminUser: String
    get() = System.getenv("KEYCLOAK_ADMIN_USERNAME")

  /**
   * Returns Keycloak realm
   */
  private val realm: String
    get() = System.getenv("KEYCLOAK_REALM")

  /**
   * Returns Keycloak server URL
   */
  private val serverUrl: String
    get() = System.getenv("KEYCLOAK_URL")

  @JsonIgnoreProperties(ignoreUnknown = true)
  private class IdExtract {
    var id: String? = null
  }
}
