package fi.metatavu.cityloops.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * JPA entity representing category
 *
 * @author Jari Nykänen
 */
@Entity
class Category {

  @Id
  var id: UUID? = null

  @NotEmpty
  @Column(nullable = false)
  var name: String? = null

  @Column(nullable = true)
  var parentCategoryId: UUID? = null

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