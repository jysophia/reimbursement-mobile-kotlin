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
    fun createReceiptObject(resultText: String, bitmap: Bitmap, uri: Uri): Receipt {
        val textArray = resultText.split("\n")
        // Instantiate a receipt
        val receipt = Receipt("", 0.00, "", uri)

        // Look for total amount
        var totalAmount = 0.00
        try {
            totalAmount = findTotalAmount(textArray)
            receipt.setCost(totalAmount)
        } catch (e: Exception) {
            receipt.setCost(totalAmount)
        }

        // Look for date
        var date: Any = LocalDate.now()
        try {
            date = findDate(textArray)
            receipt.setDate(date)
        } catch (e: Exception) {
            receipt.setDate(date)
        }

        receipt.setImageData(bitmap)
        receipt.setUri(uri)
        return receipt
    }

    fun findTotalAmount(lines: List<String>): Double {
        val format = Regex("^\\\$?(\\d{1,}(\\.\\d{2}))\$")
        val cardPaymentKeywords = listOf("payment", "total")
        val cashPaymentKeywords = listOf("change due", "cash usd", "cash cad")
        if (lines.any { line -> cashPaymentKeywords.contains(line.lowercase())}) {
            return cashPayment(lines, format)
        } else {
            return cardPayment(lines, format)
        }
    }

    private fun cardPayment(lines: List<String>, regex: Regex): Double {
        var total = 0.00
        for (line in lines) {
            if (line.matches(regex)) {
                var cost = 0.00
                if (line.contains("$")) {
                    val substring = line.substring(1, line.length)
                    cost = substring.toDouble()
                } else {
                    cost = line.toDouble()
                }
                if (total < cost) {
                    total = cost
                }
            }
        }

        return total
    }

    private fun cashPayment(lines: List<String>, regex: Regex): Double {
        var total = 0.00
        // find top 3 highest values
        // add the rest and get sum
        // return the sum
        val prices = mutableListOf<Double>()
        for (line in lines) {
            if (line.matches(regex)) {
                if (line.contains("$")) {
                    prices.add(line.substring(1, line.length).toDouble())
                } else {
                    prices.add(line.toDouble())
                }
            }
        }
        val lastThree = prices.takeLast(3)
        val restOfPrices = prices.dropLast(3)
        for (price in restOfPrices.reversed()) {
            total += price
            if (lastThree.contains(total)) {
                break
            }
        }
        return total
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
            "MM/dd/yyyy" to "\\b$month/$day/$year$optionalTime",
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

        if (text == "" && formatChosen == "") {
            throw ParserException("Unable to parse receipt, please try again or enter")
        }

        return convertDate(text, formatChosen)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDate(text: String, formatChosen: String): Any {
        var pattern = formatChosen
        var dateToParse = text
        var formatter : Any
        val date : Any
        // Check if there is time included in text
        val time = ".*\\b\\d{1,2}:\\d{2}(:\\d{2})?(?:\\s?[AP]M)?\\b.*"
        var textDateArray = text.split(" ").toMutableList()
        // Remove time from date
        if (text.matches(Regex(time))) {
            textDateArray = removeTime(textDateArray, time)
        }
        // Check if formatChosen is english and parse as needed
        if (formatChosen == "english") {
            pattern = "MMM dd yyyy"
            // Change date if month is written in its full name
            dateToParse = editEnglishDate(textDateArray)
            formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        } else {
            dateToParse = textDateArray.joinToString(" ")
            formatter = DateTimeFormatter.ofPattern(pattern)
        }
        date = LocalDate.parse(dateToParse, formatter)
        return date
    }

    private fun editEnglishDate(textDateArray: MutableList<String>): String {
        if (textDateArray[0].length > 3) {
            val monthAbbreviated = textDateArray[0].substring(0, 3)
            textDateArray[0] = monthAbbreviated
        }
        if (textDateArray[1].contains(",")) {
            val daySubstring = textDateArray[1].substring(0, textDateArray[1].indexOf(","))
            textDateArray[1] = daySubstring
        }
        return textDateArray.joinToString(" ")
    }

    private fun removeTime(textDateArray: MutableList<String>, time: String): MutableList<String> {
        if (textDateArray.contains("PM")) {
            textDateArray.remove("PM")
        } else if (textDateArray.contains("AM")) {
            textDateArray.remove("AM")
        }
        if (textDateArray[textDateArray.lastIndex].matches(Regex(time))) {
            textDateArray.removeAt(textDateArray.lastIndex)
        }
        return textDateArray
    }

}