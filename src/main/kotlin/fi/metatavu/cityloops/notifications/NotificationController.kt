package fi.metatavu.cityloops.notifications

import fi.metatavu.cityloops.email.EmailSender
import fi.metatavu.cityloops.email.mailgun.MailgunEmailSender
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

  /**
  * Sends notification about item found by search hound
  *
  * @param email Recipient email address
  * @param item Item that was found
  */
  fun sendSearchHoundItemFoundNotification(email: String, item: Item) {
    val uiHost: String? = System.getenv("UI_HOST")
    val notificationTitle = "Hakuagenttisi on löytänyt uuden kohteen"
    val notificationContent = """
    Hakuagenttisi on löytänyt uuden kohteen: ${item.title}.
    
    Voit tarkastella kohdetta ohessa olevan linkin kautta.
    
    ${uiHost}/item/${item.id.toString()}
    
    """.trimIndent()
    emailSender.sendMail(email, notificationTitle, notificationContent)
  }

}