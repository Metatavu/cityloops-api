package fi.metatavu.cityloops.persistence.model

import fi.metatavu.cityloops.api.spec.model.ItemType
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * JPA entity representing item
 *
 * @author Jari Nykänen
 */
@Entity
class Item {

  @Id
  var id: UUID? = null

  @NotEmpty
  @Column(nullable = false)
  var title: String? = null

  @ManyToOne
  var category: Category? = null

  @Column(nullable = false)
  var onlyForCompanies: Boolean? = null

  @Column(nullable = false)
  var metadata: String? = null

  @Column(nullable = false)
  var thumbnailUrl: String? = null

  @Column(nullable = false)
  var properties: String? = null

  @Column(nullable = false)
  var price: Double? = null

  @Column(nullable = false)
  var priceUnit: String? = null

  @Column(nullable = false)
  var paymentMethod: String? = null

  @Column(nullable = false)
  var delivery: Boolean? = null

  @Column(nullable = true)
  var deliveryPrice: Double? = null

  @Column(nullable = false)
  var itemType: ItemType? = null

  @ManyToOne
  var user: User? = null

  @Column(nullable = false)
  var expired: Boolean? = null

  @Column(nullable = false)
  var expiresAt: OffsetDateTime? = null

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