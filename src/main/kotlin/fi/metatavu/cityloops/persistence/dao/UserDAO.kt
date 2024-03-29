package fi.metatavu.cityloops.persistence.dao

import fi.metatavu.cityloops.persistence.model.Category_
import java.util.*
import fi.metatavu.cityloops.persistence.model.User
import fi.metatavu.cityloops.persistence.model.User_
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for user
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class UserDAO() : AbstractDAO<User>() {

  /**
   * Creates new user
   *
   * @param id id
   * @param name user name
   * @param address user address
   * @param email user email
   * @param phoneNumber user phone number
   * @param companyAccount is this user used by company
   * @param verified us this user verified
   * @param companyId company id
   * @param officeInfo office info
   * @param coordinates office coordinates
   * @param description user description
   * @param logoUrl user logo url
   * @param creatorId creator's id
   * @return created user
   */
  fun create(
    id: UUID,
    name: String,
    address: String,
    email: String,
    phoneNumber: String,
    companyAccount: Boolean,
    verified: Boolean,
    companyId: String?,
    officeInfo: String?,
    coordinates: String?,
    description: String?,
    logoUrl: String?,
    creatorId: UUID
  ): User {
    val user = User()
    user.id = id
    user.name = name
    user.address = address
    user.email = email
    user.phoneNumber = phoneNumber
    user.companyAccount = companyAccount
    user.verified = verified
    user.companyId = companyId
    user.officeInfo = officeInfo
    user.coordinates = coordinates
    user.description = description
    user.logoUrl = logoUrl
    user.creatorId = creatorId
    user.lastModifierId = creatorId
    return persist(user)
  }

  /**
   * List categories. Can be filtered by parent user
   *
   * @param companyAccount list only company accounts
   * @param verified list only verified accounts
   * @return list of users
   */
  fun list(companyAccount: Boolean?, verified: Boolean?): List<User> {
    val entityManager = getEntityManager()
    val criteriaBuilder = entityManager.criteriaBuilder
    val criteria: CriteriaQuery<User> = criteriaBuilder.createQuery(User::class.java)
    val root: Root<User> = criteria.from(User::class.java)

    val restrictions = ArrayList<Predicate>()

    if (companyAccount != null) {
      restrictions.add(criteriaBuilder.equal(root.get(User_.companyAccount), companyAccount))
    }

    if (verified != null) {
      restrictions.add(criteriaBuilder.equal(root.get(User_.verified), verified))
    }

    criteria.select(root)
    criteria.where(*restrictions.toTypedArray())

    val query: TypedQuery<User> = entityManager.createQuery<User>(criteria)
    return query.resultList
  }

  /**
   * Updates user name
   *
   * @param user user object
   * @param name new user name
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateName(user: User, name: String, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.name = name
    return persist(user)
  }

  /**
   * Updates user address
   *
   * @param user user object
   * @param address user address
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateAddress(user: User, address: String, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.address = address
    return persist(user)
  }

  /**
   * Updates user email
   *
   * @param user user object
   * @param email new user email
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateEmail(user: User, email: String, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.email = email
    return persist(user)
  }

  /**
   * Updates user phone number
   *
   * @param user user object
   * @param phoneNumber new user phone number
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updatePhoneNumber(user: User, phoneNumber: String, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.phoneNumber = phoneNumber
    return persist(user)
  }

  /**
   * Updates user company account
   *
   * @param user user object
   * @param companyAccount new user company account value
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateCompanyAccount(user: User, companyAccount: Boolean, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.companyAccount = companyAccount
    return persist(user)
  }

  /**
   * Updates user verified
   *
   * @param user user object
   * @param verified new user verified value
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateVerified(user: User, verified: Boolean, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.verified = verified
    return persist(user)
  }

  /**
   * Updates user company ID
   *
   * @param user user object
   * @param companyId new user company ID
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateCompanyId(user: User, companyId: String?, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.companyId = companyId
    return persist(user)
  }

  /**
   * Updates user office info
   *
   * @param user user object
   * @param officeInfo new user office info
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateOfficeInfo(user: User, officeInfo: String?, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.officeInfo = officeInfo
    return persist(user)
  }

  /**
   * Updates user office coordinates
   *
   * @param user user object
   * @param coordinates new user office coordinates
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateCoordinates(user: User, coordinates: String?, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.coordinates = coordinates
    return persist(user)
  }

  /**
   * Updates user description
   *
   * @param user user object
   * @param description new user description
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateDescription(user: User, description: String?, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.description = description
    return persist(user)
  }

  /**
   * Updates user logo url
   *
   * @param user user object
   * @param logoUrl new user logo url
   * @param lastModifierId last modifier's id
   * @return updated user
   */
  fun updateLogoUrl(user: User, logoUrl: String?, lastModifierId: UUID): User {
    user.lastModifierId = lastModifierId
    user.logoUrl = logoUrl
    return persist(user)
  }
}