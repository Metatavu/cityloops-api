package fi.metatavu.cityloops.email

/**
 * Interface that describes a single email sender
 *
 * @author Heikki Kurhinen
 */
interface EmailSender {
  /**
   * Sends an email
   *
   * @param toEmail recipient's email address
   * @param subject email's subject
   * @param content email's content
   */
  fun sendMail(toEmail: String?, subject: String?, content: String?)
}
