package fi.metatavu.cityloops.persistence.dao

import java.util.*
import fi.metatavu.cityloops.persistence.model.Category
import javax.enterprise.context.ApplicationScoped

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
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created sub layout
   */
  fun create(id: UUID, name: String, creatorId: UUID, lastModifierId: UUID): Category {
    val category = Category()
    category.id = id
    category.name = name
    category.creatorId = creatorId
    category.lastModifierId = lastModifierId
    return persist(category)
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
}