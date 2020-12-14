package fi.metatavu.cityloops.schedulers

import fi.metatavu.cityloops.controllers.ItemController
import javax.ejb.Schedule
import javax.ejb.Singleton
import javax.ejb.Startup
import javax.inject.Inject

/**
 * Scheduler for marking items expired
 *
 * @author Heikki Kurhinen
 */
@Singleton
@Startup
class ItemExpirationScheduler {

    @Inject
    private lateinit var itemController: ItemController

    @Schedule(hour = "*", minute="1", info = "Every hour")
    fun checkExpiration() {
        val items = itemController.listItemsToExpire()
        items.forEach { itemController.expireItem(it) }
    }


}