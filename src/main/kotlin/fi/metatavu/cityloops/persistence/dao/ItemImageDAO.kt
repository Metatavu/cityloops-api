package fi.metatavu.cityloops.persistence.dao

import fi.metatavu.cityloops.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for item image
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ItemImageDAO() : AbstractDAO<ItemImage>() {

  /**
   * Creates new item image
   *
   * @param id id
   * @param item item
   * @param url image url
   * @return created sub layout
   */
  fun create(id: UUID, item: Item, url: String): ItemImage {
    val itemImage = ItemImage()
    itemImage.id = id
    itemImage.item = item
    itemImage.url = url
    return persist(itemImage)
  }

  /**
   * Update item image url
   *
   * @param itemImage item image
   * @param url new url path
   */
  fun updateUrl(itemImage: ItemImage, url: String): ItemImage {
    itemImage.url = url
    return persist(itemImage)
  }

  /**
   * List item images, filtered by item
   *
   * @param item item user as filter
   * @return list of item images
   */
  fun listImages(item: Item): List<ItemImage> {
    val entityManager = getEntityManager()
    val criteriaBuilder = entityManager.criteriaBuilder
    val criteria: CriteriaQuery<ItemImage> = criteriaBuilder.createQuery(ItemImage::class.java)
    val root: Root<ItemImage> = criteria.from(ItemImage::class.java)
    criteria.select(root)
    criteria.where(criteriaBuilder.equal(root.get(ItemImage_.item), item))
    val query: TypedQuery<ItemImage> = entityManager.createQuery<ItemImage>(criteria)
    return query.resultList
  }
}