package fi.metatavu.cityloops.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * JPA entity representing user
 *
 * @author Jari Nykänen
 */
@Entity
class User {

  @Id
  var id: UUID? = null

  @NotEmpty
  @Column(nullable = false)
  var name: String? = null

  @NotEmpty
  @Column(nullable = false)
  var address: String? = null

  @NotEmpty
  @Column(nullable = false)
  var email: String? = null

  @NotEmpty
  @Column(nullable = false)
  var phoneNumber: String? = null

  @Column(nullable = false)
  var companyAccount: Boolean? = null

  @Column(nullable = false)
  var verified: Boolean? = null

  @Column(nullable = true)
  var companyId: String? = null

  @Column(nullable = true)
  var officeInfo: String? = null

  @Column(nullable = true)
  var coordinates: String? = null

  @Column(nullable = false)
  var createdAt: OffsetDateTime? = null

  @Column(nullable = false)
  var modifiedAt: OffsetDateTime? = null

  @Column(nullable = false)
  var creatorId: UUID? = null

  @Column(nullable = false)
  var lastModifierId: UUID? = null

  /**
   * JPA pre-persist event handler
   */
  @PrePersist
  fun onCreate() {
    createdAt = OffsetDateTime.now()
    modifiedAt = OffsetDateTime.now()
  }

  /**
   * JPA pre-update event handler
   */
  @PreUpdate
  fun onUpdate() {
    modifiedAt = OffsetDateTime.now()
  }
}