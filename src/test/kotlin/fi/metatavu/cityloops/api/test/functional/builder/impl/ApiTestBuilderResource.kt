package fi.metatavu.cityloops.api.test.functional.impl

import com.squareup.moshi.Moshi
import fi.metatavu.cityloops.api.client.infrastructure.ApiClient
import fi.metatavu.cityloops.api.client.infrastructure.ClientException
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import org.junit.Assert

/**
 * Abstract base class for API test resource builders
 */
abstract class ApiTestBuilderResource<T, A>(testBuilder: AbstractTestBuilder<ApiClient?>?, apiClient: ApiClient) : fi.metatavu.jaxrs.test.functional.builder.AbstractApiTestBuilderResource<T, A, ApiClient?>(testBuilder) {

    private val apiClient: ApiClient = apiClient

    /**
     * Returns API client
     *
     * @return API client
     */
    override fun getApiClient(): ApiClient {
        return apiClient
    }

    /**
     * Asserts that client exception has expected status code
     *
     * @param expectedStatus expected status code
     * @param e client exception
     */
    protected fun assertClientExceptionStatus(expectedStatus: Int, e: ClientException) {
        Assert.assertEquals(expectedStatus, getClientExceptionError(e)?.message)
    }

    /**
     * Returns an error response from client exception
     *
     * @param e client exception
     * @return an error response
     */
    private fun getClientExceptionError(e: ClientException): Error? {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(Error::class.java)
        val message = e.message ?: return null
        return jsonAdapter.fromJson(message)
    }
}