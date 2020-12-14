package fi.metatavu.cityloops.email.mailgun

import fi.metatavu.cityloops.email.EmailSender
import net.sargue.mailgun.Configuration
import net.sargue.mailgun.Mail
import org.apache.commons.lang3.StringUtils
import javax.enterprise.context.ApplicationScoped

/**
 * Mailgun email sender implementation
 *
 * @author Heikki Kurhinen
 */
@ApplicationScoped
class MailgunEmailSender : EmailSender {

    override fun sendMail(toEmail: String?, subject: String?, content: String?) {
        val domain: String? = System.getenv(DOMAIN_ENV)
        if (StringUtils.isEmpty(domain)) {
            return
        }
        val apiKey: String? = System.getenv(APIKEY_ENV)
        if (StringUtils.isEmpty(apiKey)) {
            return
        }
        val senderName: String? = System.getenv(SENDER_NAME_ENV)
        if (StringUtils.isEmpty(senderName)) {
            return
        }
        val senderEmail: String? = System.getenv(SENDER_EMAIL_ENV)
        if (StringUtils.isEmpty(senderEmail)) {
            return
        }
        val apiUrl: String? = System.getenv(APIURL_ENV)
        val configuration: Configuration = Configuration()
                .domain(domain)
                .apiKey(apiKey)
                .from(senderName, senderEmail)

        if (StringUtils.isNotEmpty(apiUrl)) {
            configuration.apiUrl(apiUrl)
        }

        Mail.using(configuration)
                .to(toEmail)
                .subject(subject)
                .text(content)
                .build()
                .send()
    }

    private companion object {
        const val APIURL_ENV = "MAILGUN_APIURL"
        const val DOMAIN_ENV = "MAILGUN_DOMAIN"
        const val APIKEY_ENV = "MAILGUN_APIKEY"
        const val SENDER_EMAIL_ENV = "MAILGUN_SENDER_EMAIL"
        const val SENDER_NAME_ENV = "MAILGUN_SENDER_NAME"
    }
}