package fi.metatavu.cityloops.persistence.dao

import fi.metatavu.cityloops.persistence.model.Category
import java.util.*
import fi.metatavu.cityloops.persistence.model.Item
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

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
   * @param metadata item metadata as string
   * @param thumbnailUrl item thumbnail url
   * @param properties item key value property pairs as string
   * @param creatorId creator's id
   * @return created sub layout
   */
  fun create(
    id: UUID,
    title: String,
    category: Category,
    onlyForCompanies: Boolean,
    metadata: String?,
    thumbnailUrl: String?,
    properties: String?,
    creatorId: UUID
  ): Item {
    val item = Item()
    item.id = id
    item.title = title
    item.category = category
    item.onlyForCompanies = onlyForCompanies
    item.metadata = metadata
    item.thumbnailUrl = thumbnailUrl
    item.properties = properties
    item.creatorId = creatorId
    item.lastModifierId = creatorId
    return persist(item)
  }

  /**
   * Updates item title
   *
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
   * Updates item thumbnail url
   *
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
   * Updates item properties
   *
   * @param properties item key value property pairs as string
   * @param lastModifierId last modifier's id
   * @return updated item
   */
  fun updateProperties(item: Item, properties: String, lastModifierId: UUID): Item {
    item.properties = properties
    item.lastModifierId = lastModifierId
    return persist(item)
  }
}