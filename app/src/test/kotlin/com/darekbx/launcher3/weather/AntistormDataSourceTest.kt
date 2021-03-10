package com.darekbx.launcher3.weather

import android.graphics.Bitmap
import com.darekbx.launcher3.utils.HttpTools
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AntistormDataSourceTest {

    private val radarData =
        """timestamp:1614942602,1614942001<br>
            |nazwa_folderu:2021.3.5,2021.3.5<br>
            |nazwa_pliku:12-10,12-0<br>
            |nazwa_pliku_front:20210305.118,20210305.1058<br>
            |aktywnosc_burz:572,511<br>""".cleanForTests()

    private val stormData =
        """timestamp:1614946801,1614946201<br>
        |nazwa_folderu:blank,blank<br>
        |nazwa_pliku:blank,blank<br>
        |nazwa_pliku_front:20210305.1218,20210305.128<br>
        |aktywnosc_burz:0,0<br>""".cleanForTests()

    private fun String.cleanForTests() = this.trimMargin().replace("\n", "")

    private val httpTools = mock<HttpTools> {
        on { downloadString("https://antistorm.eu/ajaxPaths.php?lastTimestamp=0&type=radar") } doReturn (radarData)
        on { downloadString("https://antistorm.eu/ajaxPaths.php?lastTimestamp=0&type=storm") } doReturn (stormData)
        on { downloadImage(any()) } doReturn(Bitmap.createBitmap(15, 15, Bitmap.Config.ARGB_8888))
    }

    @Test
    fun `Image was properly generated`() = runBlocking {
        // Given
        val positionMarker = mock<PositionMarker>()
        val antistormDataSource = AntistormDataSource(httpTools, positionMarker)

        // When
        val result = antistormDataSource.downloadRainPrediction(0.0, 0.0)

        // Then
        val pathsUrlCaptor = argumentCaptor<String>()
        verify(httpTools, times(2)).downloadString(pathsUrlCaptor.capture())
        assertTrue(pathsUrlCaptor.firstValue.endsWith("=radar"))
        assertTrue(pathsUrlCaptor.secondValue.endsWith("=storm"))

        val imageUrlCaptor = argumentCaptor<String>()
        verify(httpTools, times(3)).downloadImage(imageUrlCaptor.capture())
        assertTrue(imageUrlCaptor.firstValue.contains("visualPhenom/20210305.118-radar-visualPhenomenon"))
        assertTrue(imageUrlCaptor.secondValue.contains("archive/2021.3.5/12-10-radar-probabilitiesImg"))
        assertTrue(imageUrlCaptor.thirdValue.contains("visualPhenom/20210305.1218-storm-visualPhenomenon"))

        assertEquals(15, result.width)
        assertEquals(15, result.height)
    }
}
