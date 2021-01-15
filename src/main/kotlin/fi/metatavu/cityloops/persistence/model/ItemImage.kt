package fi.metatavu.cityloops.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * JPA entity representing item image
 *
 * @author Jari Nykänen
 */
@Entity
class ItemImage {

  @Id
  var id: UUID? = null

  @ManyToOne
  var item: Item? = null

  @NotEmpty
  @Column(nullable = false)
  var url: String? = null

}