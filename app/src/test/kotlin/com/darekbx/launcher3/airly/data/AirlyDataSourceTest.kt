package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.airly.domain.ResponseWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

@ExperimentalCoroutinesApi
class AirlyDataSourceTest {

    private val httpTools = AirlyDataSource.AirlyHttpTools("api-key")

    @Test
    fun `Download two installations`() = runBlocking {
        // Given
        val mockServer = MockWebServer()
        readResourceFile("installations.json")?.use {
            mockServer.enqueue(MockResponse().setBody(Buffer().readFrom(it)))
        } ?: fail("Unable to read instsallation.json file")

        val airlyDataSource = AirlyDataSource(httpTools, mockServer.url(""))

        // When
        val installations = airlyDataSource.readInstallations(10.0, 20.0, 1.0, 1)

        // Then
        val request = mockServer.takeRequest()
        assertEquals(
            "/v2/installations/nearest?lat=10.0&lng=20.0&maxDistanceKm=1.0&maxResults=1",
            request.path
        )
        installations.value?.let { responseWrapper ->
            assertEquals(2, responseWrapper.size)

            with(responseWrapper[0]) {
                assertEquals(9935, id)
                assertEquals(52.2387, location.latitude, 0.001)
                assertEquals(20.8952, location.longitude, 0.001)
            }

            with(responseWrapper[1]) {
                assertEquals(12040, id)
                assertEquals(52.2502, location.latitude, 0.001)
                assertEquals(20.9098, location.longitude, 0.001)
            }
        } ?: fail("Response is null")
    }

    @Test
    fun `Download one valid and one failed measurement by ids`() = runBlocking {
        // Given
        val mockServer = MockWebServer()
        readResourceFile("measurements.json")?.use {
            val response = MockResponse()
                .setHeader("x-ratelimit-limit-day", 1000)
                .setHeader("x-ratelimit-remaining-day", 900)
                .setHeader("x-ratelimit-limit-minute", 50)
                .setHeader("x-ratelimit-remaining-minute", 10)
                .setBody(Buffer().readFrom(it))
            mockServer.enqueue(response)
        } ?: fail("Unable to read measurements.json file")
        mockServer.enqueue(MockResponse().setResponseCode(500))

        val airlyDataSource = AirlyDataSource(httpTools, mockServer.url(""))
        val measurements = mutableListOf<ResponseWrapper<Measurements>>()

        // When
        airlyDataSource.readMeasurements(listOf(1, 20)) {
            measurements.add(it)
        }

        // Then
        val request1 = mockServer.takeRequest()
        assertEquals("/v2/measurements/installation?installationId=1", request1.path)
        assertFalse(measurements[0].hasError)

        measurements[0].value?.run {
            with(current) {
                assertEquals(3, values.size)
                assertEquals(1, indexes.size)
            }

            rateLimits?.run {
                assertEquals(1000, dayLimit)
                assertEquals(900, dayRemaining)
                assertEquals(50, minuteLimit)
                assertEquals(10, minuteRemaining)
            }

            assertEquals("-5.1°", temperature)
            assertEquals("95%", humidity)
            assertEquals("203%", averagePMNorm)
        }

        val request2 = mockServer.takeRequest()
        assertEquals("/v2/measurements/installation?installationId=20", request2.path)
        assertTrue(measurements[1].hasError)
    }

    private fun readResourceFile(fileName: String) =
        javaClass.classLoader?.getResourceAsStream(fileName)
}
