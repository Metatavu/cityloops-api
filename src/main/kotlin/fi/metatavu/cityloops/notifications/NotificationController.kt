package fi.metatavu.cityloops.notifications

import fi.metatavu.cityloops.email.EmailSender
import fi.metatavu.cityloops.persistence.model.Item
import javax.enterprise.context.ApplicationScoped

/**
 * Controller for sending notifications
 *
 * @author Heikki Kurhinen
 *
 */
@ApplicationScoped
class NotificationController {

  private lateinit var emailSender: EmailSender

  /**
   * Sends item expiration notification
   *
   * @param item Item that is expiring
   */
  fun sendItemExpirationNotification(item: Item) {
    val user = item.user ?: return
    val notificationTitle = "Ilmoitus ${item.title} on vanhentunut"
    val notificationContent = """
    Ilmoitus ${item.title} on vanhentunut.
    
    Mikäli haluat uusia ilmoituksen voit tehdä sen kirjautumalla sisään ja jatkamalla ilmoitusta.
    """.trimIndent()
    emailSender.sendMail(user.email, notificationTitle, notificationContent)
  }

}