package fi.metatavu.cityloops.persistence.dao

import fi.metatavu.cityloops.persistence.model.*
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.Predicate
import kotlin.collections.ArrayList

/**
 * DAO class for search hound
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class SearchHoundDAO() : AbstractDAO<SearchHound>() {

  /**
   * Creates new search hound
   *
   * @param id id
   * @param name search hound name
   * @param category category that is monitored
   * @param notificationsOn are notifications on or not
   * @param user search hound owner
   * @param expires when this search hound expires
   * @param minPrice min price of the search hound
   * @param maxPrice max price of the search hound
   * @param creatorId creator's id
   * @return created search hound
   */
  fun create(
    id: UUID,
    name: String,
    notificationsOn: Boolean,
    category: Category,
    user: User,
    expires: OffsetDateTime,
    minPrice: Double?,
    maxPrice: Double?,
    creatorId: UUID
  ): SearchHound {
    val searchHound = SearchHound()
    searchHound.id = id
    searchHound.name = name
    searchHound.notificationsOn = notificationsOn
    searchHound.category = category
    searchHound.user = user
    searchHound.expires = expires
    searchHound.minPrice = minPrice
    searchHound.maxPrice = maxPrice
    searchHound.creatorId = creatorId
    searchHound.lastModifierId = creatorId
    return persist(searchHound)
  }

  /**
   * Updates search hound name
   *
   * @param name new search hound name
   * @param lastModifierId last modifier's id
   * @return updated search hound
   */
  fun updateName(searchHound: SearchHound, name: String, lastModifierId: UUID): SearchHound {
    searchHound.name = name
    searchHound.lastModifierId = lastModifierId
    return persist(searchHound)
  }

  /**
   * Updates search hound category
   *
   * @param category category where this search hound monitors
   * @param lastModifierId last modifier's id
   * @return updated search hound
   */
  fun updateCategory(searchHound: SearchHound, category: Category, lastModifierId: UUID): SearchHound {
    searchHound.category = category
    searchHound.lastModifierId = lastModifierId
    return persist(searchHound)
  }

  /**
   * Updates search hound notification on
   *
   * @param notificationsOn are notification on or not
   * @param lastModifierId last modifier's id
   * @return updated search hound
   */
  fun updateNotificationsOn(searchHound: SearchHound, notificationsOn: Boolean, lastModifierId: UUID): SearchHound {
    searchHound.notificationsOn = notificationsOn
    searchHound.lastModifierId = lastModifierId
    return persist(searchHound)
  }

  /**
   * Updates search hound min price
   *
   * @param minPrice search hound min price
   * @param lastModifierId last modifier's id
   * @return updated search hound
   */
  fun updateMinPrice(searchHound: SearchHound, minPrice: Double?, lastModifierId: UUID): SearchHound {
    searchHound.minPrice = minPrice
    searchHound.lastModifierId = lastModifierId
    return persist(searchHound)
  }

  /**
   * Updates search hound max price
   *
   * @param maxPrice search hound min price
   * @param lastModifierId last modifier's id
   * @return updated search hound
   */
  fun updateMaxPrice(searchHound: SearchHound, maxPrice: Double?, lastModifierId: UUID): SearchHound {
    searchHound.maxPrice = maxPrice
    searchHound.lastModifierId = lastModifierId
    return persist(searchHound)
  }

  /**
   * Updates search hound expiration
   *
   * @param expires when this search hound expires
   * @param lastModifierId last modifier's id
   * @return updated search hound
   */
  fun updateExpires(searchHound: SearchHound, expires: OffsetDateTime, lastModifierId: UUID): SearchHound {
    searchHound.expires = expires
    searchHound.lastModifierId = lastModifierId
    return persist(searchHound)
  }

  /**
   * List search hounds
   *
   * @param user filter by user
   * @param category filter by category
   * @param notificationsOn are notification on or not
   * @return list of search hounds
   */
  fun list(user: User?, category: Category?, notificationsOn: Boolean?): List<SearchHound> {
    val entityManager = getEntityManager()
    val criteriaBuilder = entityManager.criteriaBuilder
    val criteria = criteriaBuilder.createQuery(SearchHound::class.java)
    val root = criteria.from(SearchHound::class.java)
    val restrictions = ArrayList<Predicate>()

    if (user != null) {
      restrictions.add(criteriaBuilder.equal(root.get(SearchHound_.user), user))
    }

    if (category != null) {
      restrictions.add(criteriaBuilder.equal(root.get(SearchHound_.category), category))
    }

    if (notificationsOn != null) {
      restrictions.add(criteriaBuilder.equal(root.get(SearchHound_.notificationsOn), notificationsOn))
    }

    criteria.select(root)
    criteria.where(*restrictions.toTypedArray())

    val query: TypedQuery<SearchHound> = entityManager.createQuery<SearchHound>(criteria)
    return query.resultList
  }
}