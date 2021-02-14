package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.CoroutineTestRule
import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.airly.domain.ResponseWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AirlyDataSourceTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Test
    fun `Download two installations`() = runBlocking {
        // Given
        val mockServer = MockWebServer()
        mockServer.enqueue(MockResponse().setBody("[{\"id\":9935,\"location\":{\"latitude\":52.238789,\"longitude\":20.895262},\"locationId\":9935,\"address\":{\"country\":\"Poland\",\"city\":\"Warsaw\",\"street\":\"Górczewska\",\"number\":\"255C\",\"displayAddress1\":\"Warsaw\",\"displayAddress2\":\"Górczewska\"},\"elevation\":106.56,\"airly\":true,\"sponsor\":{\"id\":556,\"name\":\"Zostań ambasadorem tego czujnika\",\"description\":\"Airly Sensor is part of action\",\"logo\":\"https://cdn.airly.eu/logo/img.png\",\"link\":\"https://airly.eu/contact/\",\"displayName\":\"Zostań ambasadorem tego czujnika\"}},{\"id\":12040,\"location\":{\"latitude\":52.250287,\"longitude\":20.909821},\"locationId\":12040,\"address\":{\"country\":\"Poland\",\"city\":\"Warsaw\",\"street\":\"Zeusa\",\"number\":\"45\",\"displayAddress1\":\"Warsaw\",\"displayAddress2\":\"Zeusa\"},\"elevation\":108.71,\"airly\":true,\"sponsor\":{\"id\":690,\"name\":\"FOCUS Real Estates\",\"description\":\"Airly Sensor's sponsor\",\"logo\":\"https://cdn.airly.eu/logo/img.jpg\",\"link\":null,\"displayName\":\"FOCUS Real Estates\"}}]"))

        val apiKey = "api-key"
        val airlyDataSource = AirlyDataSource(apiKey, mockServer.url(""))

        // When
        val installations = airlyDataSource.readInstallations(10.0, 20.0, 1.0, 1)

        // Then
        val request = mockServer.takeRequest()
        assertEquals("/v2/installations/nearest?lat=10.0&lng=20.0&maxDistanceKm=1.0&maxResults=1", request.path)
        assertEquals(2, installations.value?.size)

        with (installations.value!![0]) {
            assertEquals(9935, id)
            assertEquals(52.2387, location.latitude, 0.001)
            assertEquals(20.8952, location.longitude, 0.001)
        }

        with (installations.value!![1]) {
            assertEquals(12040, id)
            assertEquals(52.2502, location.latitude, 0.001)
            assertEquals(20.9098, location.longitude, 0.001)
        }
    }

    @Test
    fun `Download one valid and one failed measurement by ids`() = runBlocking {
        // Given
        val mockServer = MockWebServer()
        mockServer.enqueue(MockResponse().setBody("{\"current\":{\"fromDateTime\":\"2021-02-11T08:48:36.413Z\",\"tillDateTime\":\"2021-02-11T09:48:36.413Z\",\"values\":[{\"name\":\"PM1\",\"value\":20.91}],\"indexes\":[{\"name\":\"AIRLY_CAQI\",\"value\":51.37,\"level\":\"MEDIUM\",\"description\":\"Good\",\"advice\":\"Protect\",\"color\":\"#EFBB0F\"}],\"standards\":[{\"name\":\"WHO\",\"pollutant\":\"PM25\",\"limit\":25.0,\"percent\":141.22,\"averaging\":\"24h\"}]},\"history\":[]}"))
        mockServer.enqueue(MockResponse().setResponseCode(500))

        val apiKey = "api-key"
        val airlyDataSource = AirlyDataSource(apiKey, mockServer.url(""))
        val measurements = mutableListOf<ResponseWrapper<Measurements>>()

        // When
        airlyDataSource.readMeasurements(1, 20) {
            measurements.add(it)
        }

        // Then
        val request1 = mockServer.takeRequest()
        assertEquals("/v2/measurements/installation?installationId=1", request1.path)
        assertFalse(measurements[0].hasError)

        measurements[0].value?.current?.run {
            assertEquals(1, values.size)
            assertEquals(1, indexes.size)
        }

        val request2 = mockServer.takeRequest()
        assertEquals("/v2/measurements/installation?installationId=20", request2.path)
        assertTrue(measurements[1].hasError)
    }
}
