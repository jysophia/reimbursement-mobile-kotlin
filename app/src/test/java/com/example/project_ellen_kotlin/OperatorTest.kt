package com.example.project_ellen_kotlin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.BufferedReader
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
class OperatorTest {
    private lateinit var context: Context
    private lateinit var operator: Operator
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
        operator = Operator()
        assertNotNull(operator)
    }

    fun loadResultText(fileName: String): String {
        val inputStream = context.resources.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.use { it.readText() }
    }

    @Test
    fun testParseEnglishDateWithCommaAndTime() {
        val resultText = loadResultText("sampleResultText/resultTextEnglishDateWithCommaAndTime")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-10-14", finalDateString)
    }

    @Test
    fun testParseEnglishDateNoCommaNoTime() {
        val resultText = loadResultText("sampleResultText/resultTextEnglishDateNoCommaNoTime")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-10-04", finalDateString)
    }

    @Test
    fun testParseEnglishDateFullMonthName() {
        val resultText = loadResultText("sampleResultText/resultTextEnglishDateFullMonthName")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-10-04", finalDateString)
    }

    @Test
    fun testParseDateFormatDashed_yyyyMMdd() {
        val resultText = loadResultText("sampleResultText/resultTextDashedDate_yyyyMMdd")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-03", finalDateString)
    }

    @Test
    fun testParseDateFormatDashed_ddMMyyyy() {
        val resultText = loadResultText("sampleResultText/resultTextDashedDate_ddMMyyyy")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-03", finalDateString)
    }

    @Test
    fun testParseDateFormatDashed_MMddyyyy() {
        val resultText = loadResultText("sampleResultText/resultTextDashedDate_MMddyyyy")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-30", finalDateString)
    }

    @Test
    fun testParseDateFormatSlash_MMddyyyy() {
        val resultText = loadResultText("sampleResultText/resultTextSlashDate_MMddyyyy")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-30", finalDateString)
    }

    @Test
    fun testParseDateFormatSlash_ddMMyyyy() {
        val resultText = loadResultText("sampleResultText/resultTextSlashDate_ddMMyyyy")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-30", finalDateString)
    }

    @Test
    fun testParseDateFormatSlash_yyyyMMdd() {
        val resultText = loadResultText("sampleResultText/resultTextSlashDate_yyyyMMdd")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-30", finalDateString)
    }

    @Test
    fun testParseDateFormatDot_yyyyMMdd() {
        val resultText = loadResultText("sampleResultText/resultTextDotDate_yyyyMMdd")
        val textArray = resultText.split("\n")
        val date = operator.findDate(textArray)
        val finalDateString = date.toString()
        assertEquals("2024-08-30", finalDateString)
    }

    @Test
    fun testFindTotalAmountWithDollarSign() {
        val resultText = loadResultText("sampleResultText/resultTextTotalAmountWithDollarSign")
        val textArray = resultText.split("\n")
        val amount = operator.findTotalAmount(textArray)
        assertEquals(118.43, amount, 0.0)
    }

    @Test
    fun testFindTotalAmountNoDollarSign() {
        val resultText = loadResultText("sampleResultText/resultTextTotalAmountNoDollarSign")
        val textArray = resultText.split("\n")
        val amount = operator.findTotalAmount(textArray)
        assertEquals(118.43, amount, 0.0)
    }
}