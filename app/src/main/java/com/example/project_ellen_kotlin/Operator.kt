package com.example.project_ellen_kotlin

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class Operator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseResultText(resultText: String, bitmap: Bitmap, uri: Uri): Receipt {
        var textArray = resultText.split("\n")
        // Look for total amount
        val totalAmount = findTotalAmount(textArray)
        // Look for date
        val date = findDate(textArray)
        val receipt = createReceiptObject(date, totalAmount, bitmap, uri)
        return receipt
    }

    fun findTotalAmount(textArray: List<String>): Double {
        var maximum = 0.00
        val format = "^\\\$?(\\d{1,}(\\.\\d{2}))\$"
        for (text in textArray) {
            if (text.matches(Regex(format))) {
                var cost = 0.00
                if (text.contains("$")) {
                    val substring = text.substring(1, text.length)
                    cost = substring.toDouble()
                } else {
                    cost = text.toDouble()
                }
                if (maximum < cost) {
                    maximum = cost
                }
            }
        }
        return maximum
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun findDate(textArray: List<String>): Any {
        var text = ""
        var formatChosen = ""
        val day = "(0?[1-9]|1[0-9]|2[0-9]|3[0-1]),?"
        val month = "(0?[1-9]|1[0-2])"
        val year = "(202[4-9]|20[3-9]\\d|2100|2[1-9]\\d{2}|3000)"
        val optionalTime = "\\b(?: \\d{1,2}:\\d{2}(?:\\s[AP]M)?)?"

        val formatDict = mapOf(
            "yyyy-MM-dd" to "\\b$year-$month-$day$optionalTime",
            "dd-MM-yyyy" to "\\b$day-$month-$year$optionalTime",
            "MM-dd-yyyy" to "\\b$month-$day-$year$optionalTime",
            "MM/dd/yyyy" to "\\b$month/$day/$year$optionalTime",
            "dd/MM/yyyy" to "\\b$day/$month/$year$optionalTime",
            "yyyy/MM/dd" to "\\b$year/$month/$day$optionalTime",
            "yyyy.MM.dd" to "\\b$year\\.$month\\.$day$optionalTime",
            "english" to "\\b(?:Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(t|tember)?|(O|0)ct(ober)?|Nov(ember)?|Dec(ember)?) $day $year$optionalTime"
        )


        for (t in textArray) {
            for ((f, reg) in formatDict) {
                if (t.matches(Regex(reg))) {
                    text = t
                    formatChosen = f
                    break
                }
            }
            if (text.length >= 10) {
                break
            }
        }

        val date = convertDate(formatDict, text, formatChosen)
        return date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDate(formatDict: Map<String, String>, text: String, formatChosen: String): Any {
        var pattern = formatChosen
        var dateToParse = text
        var formatter : Any
        val date : Any
        // Check if there is time included in text
        val time = ".*\\b\\d{1,2}:\\d{2}(?:\\s[AP]M)?\\b.*"
        var textDateArray = text.split(" ").toMutableList()
        // Remove time from date
        if (text.matches(Regex(time))) {
            if (textDateArray.contains("PM")) {
                textDateArray.remove("PM")
            } else if (textDateArray.contains("AM")) {
                textDateArray.remove("AM")
            }
            if (textDateArray[textDateArray.lastIndex].matches(Regex(time))) {
                textDateArray.removeAt(textDateArray.lastIndex)
            }
            dateToParse = textDateArray.joinToString(" ")
        }
        // Check if formatChosen is english and parse as needed
        if (formatChosen == "english") {
            pattern = "MMM dd yyyy"
            // Change date if month is written in its full name
            if (textDateArray[0].length > 3) {
                val monthAbbreviated = textDateArray[0].substring(0, 3)
                textDateArray[0] = monthAbbreviated
                dateToParse = textDateArray.joinToString(" ")
            }
            if (textDateArray[1].contains(",")) {
                val daySubstring = textDateArray[1].substring(0, textDateArray[1].length-1)
                textDateArray[1] = daySubstring
                dateToParse = textDateArray.joinToString(" ")
            }
            formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        } else {
            formatter = DateTimeFormatter.ofPattern(pattern)
        }
        date = LocalDate.parse(dateToParse, formatter)
        return date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createReceiptObject(date: Any, amount: Double, bitmap: Bitmap, uri: Uri): Receipt {
        val receipt = Receipt(date, amount, "", uri)
        receipt.setDate(date)
        receipt.setPrice(amount)
        receipt.setUri(uri)
        receipt.setImageData(bitmap)
        return receipt
    }
}