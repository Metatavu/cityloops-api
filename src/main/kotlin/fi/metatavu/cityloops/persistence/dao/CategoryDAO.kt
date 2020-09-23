package fi.metatavu.cityloops.persistence.dao

import java.util.*
import fi.metatavu.cityloops.persistence.model.Category
import fi.metatavu.cityloops.persistence.model.Category_
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for category
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class CategoryDAO() : AbstractDAO<Category>() {

  /**
   * Creates new category
   *
   * @param id id
   * @param name category name
   * @param parentCategory parent category
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created user
   */
  fun create(id: UUID, name: String, parentCategory: Category?, creatorId: UUID, lastModifierId: UUID): Category {
    val category = Category()
    category.id = id
    category.name = name
    category.parentCategoryId = parentCategory?.id
    category.creatorId = creatorId
    category.lastModifierId = lastModifierId
    return persist(category)
  }

  /**
   * List categories. Can be filtered by parent category
   *
   * @param parentCategory parent category
   */
  fun list(parentCategory: Category?): List<Category> {
    val entityManager = getEntityManager()
    val criteriaBuilder = entityManager.criteriaBuilder
    val criteria: CriteriaQuery<Category> = criteriaBuilder.createQuery(Category::class.java)
    val root: Root<Category> = criteria.from(Category::class.java)

    val restrictions = ArrayList<Predicate>()

    if (parentCategory != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Category_.parentCategoryId), parentCategory.id))
    }

    criteria.select(root)
    criteria.where(*restrictions.toTypedArray())

    val query: TypedQuery<Category> = entityManager.createQuery<Category>(criteria)
    return query.resultList
  }

  /**
   * Updates category name
   *
   * @param name new category name
   * @param lastModifierId last modifier's id
   * @return updated category
   */
  fun updateName(category: Category, name: String, lastModifierId: UUID): Category {
    category.lastModifierId = lastModifierId
    category.name = name
    return persist(category)
  }

  /**
   * Updates parent category
   *
   * @param parentCategory parent category name
   * @param lastModifierId last modifier's id
   * @return updated category
   */
  fun updateParentCategory(category: Category, parentCategory: Category?, lastModifierId: UUID): Category {
    category.lastModifierId = lastModifierId
    category.parentCategoryId = parentCategory?.id
    return persist(category)
  }
}