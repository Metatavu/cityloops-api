package fi.metatavu.cityloops.persistence.dao

import fi.metatavu.cityloops.api.spec.model.ItemType
import fi.metatavu.cityloops.persistence.model.*
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.Predicate
import kotlin.collections.ArrayList

/**
 * DAO class for item
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ItemDAO() : AbstractDAO<Item>() {

  /**
   * Creates new item
   *
   * @param id id
   * @param title item title
   * @param category category where this item belongs to
   * @param onlyForCompanies is this item available only for companies
   * @param user item owner
   * @param metadata item metadata as string
   * @param itemType item type
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs as string
   * @param price price of the item
   * @param priceUnit price unit of the item
   * @param paymentMethod item payment method
   * @param delivery item delivery
   * @param deliveryPrice delivery price
   * @param creatorId creator's id
   * @return created sub layout
   */
  fun create(
    id: UUID,
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    user: User,
    metadata: String,
    itemType: ItemType,
    thumbnailUrl: String?,
    properties: String?,
    price: Double,
    priceUnit: String,
    paymentMethod: String,
    delivery: Boolean,
    deliveryPrice: Double?,
    creatorId: UUID
  ): Item {
    val item = Item()
    item.id = id
    item.title = title
    item.category = category
    item.onlyForCompanies = onlyForCompanies
    item.user = user
    item.itemType = itemType
    item.metadata = metadata
    item.thumbnailUrl = thumbnailUrl
    item.properties = properties
    item.price = price
    item.priceUnit = priceUnit
    item.paymentMethod = paymentMethod
    item.delivery = delivery
    item.deliveryPrice = deliveryPrice
    item.creatorId = creatorId
    item.lastModifierId = creatorId
    item.expired = false
    item.expiresAt = OffsetDateTime.now().plusDays(30)
    return persist(item)
  }

  /**
   * Updates item title
   *
   * @param item item
   * @param title new item title
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateTitle(item: Item, title: String, lastModifierId: UUID): Item {
    item.title = title
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item category
   *
   * @param item item
   * @param category category where this item belongs to
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateCategory(item: Item, category: Category, lastModifierId: UUID): Item {
    item.category = category
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item only for companies boolean
   *
   * @param item item
   * @param onlyForCompanies is this item available only for companies
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateOnlyForCompanies(item: Item, onlyForCompanies: Boolean, lastModifierId: UUID): Item {
    item.onlyForCompanies = onlyForCompanies
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item metadata
   *
   * @param item item
   * @param metadata item metadata
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateMetadata(item: Item, metadata: String, lastModifierId: UUID): Item {
    item.metadata = metadata
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item type
   *
   * @param item item
   * @param itemType item type
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateItemType(item: Item, itemType: ItemType, lastModifierId: UUID): Item {
    item.itemType = itemType
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item thumbnail url
   *
   * @param item item
   * @param thumbnailUrl item thumbnail url
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateThumbnailUrl(item: Item, thumbnailUrl: String?, lastModifierId: UUID): Item {
    item.thumbnailUrl = thumbnailUrl
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item price
   *
   * @param item item
   * @param price price of the item
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updatePrice(item: Item, price: Double, lastModifierId: UUID): Item {
    item.price = price
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item price unit
   *
   * @param item item
   * @param priceUnit item price unit
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updatePriceUnit(item: Item, priceUnit: String, lastModifierId: UUID): Item {
    item.priceUnit = priceUnit
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item expired status
   *
   * @param item item to update
   * @param expired is item expired or not
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateExpires(item: Item, expired: Boolean, lastModifierId: UUID?): Item {
    item.expired = expired
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item expiration date
   *
   * @param item item to update
   * @param expiresAt datetime item expires
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateExpiresAt(item: Item, expiresAt: OffsetDateTime, lastModifierId: UUID?): Item {
    item.expiresAt = expiresAt
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item payment method
   *
   * @param item item
   * @param paymentMethod item payment method
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updatePaymentMethod(item: Item, paymentMethod: String, lastModifierId: UUID): Item {
    item.paymentMethod = paymentMethod
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item delivery
   *
   * @param item item
   * @param delivery item delivery
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateDelivery(item: Item, delivery: Boolean, lastModifierId: UUID): Item {
    item.delivery = delivery
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item delivery price
   *
   * @param item item
   * @param deliveryPrice item delivery price
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateDeliveryPrice(item: Item, deliveryPrice: Double?, lastModifierId: UUID): Item {
    item.deliveryPrice = deliveryPrice
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Updates item properties
   *
   * @param item item
   * @param properties item key value property pairs as string
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateProperties(item: Item, properties: String, lastModifierId: UUID): Item {
    item.properties = properties
    item.lastModifierId = lastModifierId
    return persist(item)
  }

  /**
   * Lists items that have their expiration date due and that are not yet expired
   *
   * @return list of items to expire
   */
  fun listItemsToExpire(): List<Item> {
    val entityManager = getEntityManager()
    val criteriaBuilder = entityManager.criteriaBuilder
    val criteria = criteriaBuilder.createQuery(Item::class.java)
    val root = criteria.from(Item::class.java)
    val restrictions = ArrayList<Predicate>()
    restrictions.add(criteriaBuilder.equal(root.get(Item_.expired), false))
    restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(Item_.expiresAt), OffsetDateTime.now()))
    criteria.select(root)
    criteria.where(*restrictions.toTypedArray())
    val query: TypedQuery<Item> = entityManager.createQuery<Item>(criteria)
    return query.resultList
  }

  /**
   * List items
   *
   * @param firstResult index of the first result
   * @param maxResults limit amount of results to this number
   * @param sortByDateReturnOldestFirst return oldest first
   * @param user filter by user
   * @param category filter by category
   * @param includeExpired include expired items
   * @param itemType filter by item type
   * @return list of items
   */
  fun list(
    firstResult: Int?,
    maxResults: Int?,
    sortByDateReturnOldestFirst: Boolean?,
    user: User?,
    category: Category?,
    includeExpired: Boolean?,
    itemType: ItemType?
  ): List<Item> {
    val entityManager = getEntityManager()
    val criteriaBuilder = entityManager.criteriaBuilder
    val criteria = criteriaBuilder.createQuery(Item::class.java)
    val root = criteria.from(Item::class.java)
    val restrictions = ArrayList<Predicate>()

    if (sortByDateReturnOldestFirst == true) {
      criteria.orderBy(criteriaBuilder.asc(root.get(Item_.createdAt)))
    } else {
      criteria.orderBy(criteriaBuilder.desc(root.get(Item_.createdAt)))
    }

    if (user != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Item_.user), user))
    }

    if (category != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Item_.category), category))
    }

    if (itemType != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Item_.itemType), itemType))
    }

    restrictions.add(criteriaBuilder.equal(root.get(Item_.expired), includeExpired ?: false))

    criteria.select(root)
    criteria.where(*restrictions.toTypedArray())

    val query: TypedQuery<Item> = entityManager.createQuery<Item>(criteria)
    if (firstResult != null) {
      query.firstResult = firstResult
    }

    if (maxResults != null) {
      query.maxResults = maxResults
    }
    return query.resultList
  }
}