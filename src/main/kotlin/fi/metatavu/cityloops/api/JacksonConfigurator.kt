package fi.metatavu.cityloops.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

/**
 * Jackson configurator for RESTEasy
 *
 * @author Jari Nykänen
 */
@Provider
class JacksonConfigurator : ContextResolver<ObjectMapper> {

  override fun getContext(type: Class<*>?): ObjectMapper {
    val objectMapper = ObjectMapper()
    objectMapper.registerModule(JavaTimeModule())
    objectMapper.registerModule(KotlinModule())
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    return objectMapper
  }

}