package fi.metatavu.cityloops.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * JPA entity representing searchHound
 *
 * @author Jari Nykänen
 */
@Entity
class SearchHound {

  @Id
  var id: UUID? = null

  @NotEmpty
  @Column(nullable = false)
  var name: String? = null

  @Column(nullable = false)
  var notificationsOn: Boolean? = null

  @ManyToOne
  var category: Category? = null

  @ManyToOne
  var user: User? = null

  @Column(nullable = true)
  var expires: OffsetDateTime? = null

  @Column(nullable = true)
  var minPrice: Double? = null

  @Column(nullable = true)
  var maxPrice: Double? = null

  @Column(nullable = false)
  var createdAt: OffsetDateTime? = null

  @Column(nullable = false)
  var modifiedAt: OffsetDateTime? = null

  @Column(nullable = false)
  var expiresAt: OffsetDateTime? = null

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
    expiresAt = OffsetDateTime.now().plusDays(30)
  }

  /**
   * JPA pre-update event handler
   */
  @PreUpdate
  fun onUpdate() {
    modifiedAt = OffsetDateTime.now()
  }
}