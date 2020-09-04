package fi.metatavu.cityloops.api

import fi.metatavu.cityloops.api.spec.SystemApi

import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.ws.rs.core.Response


/**
 * System API REST endpoints
 *
 * @author Jari Nykänen
 */
@RequestScoped
@Stateful
open class SystemApiImpl(): SystemApi {

  override fun ping(): Response? {
    return Response.ok("pong").build()
  }

}
