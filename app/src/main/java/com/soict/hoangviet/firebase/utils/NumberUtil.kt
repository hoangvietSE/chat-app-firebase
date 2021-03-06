package com.soict.hoangviet.firebase.utils

import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object NumberUtil {
    val formatter = DecimalFormat("#,###,###,###", DecimalFormatSymbols(Locale.US))

    fun formatValue(value: String): String {
        try {
            return formatter.format(removeSpecialCharacters(value).toInt())
        } catch (e: NumberFormatException) {
            return ""
        }
    }

    private fun removeSpecialCharacters(value: String): Double {
        val resultString = StringBuilder("")
        for (i in value) {
            if (i.isDigit()) {
                resultString.append(i)
            }
        }
        return resultString.toString().toDouble()
    }

    fun formatPhoneTokenSms(phoneNumer: String): String {
        if (phoneNumer[0].toString() == "0") {
            return "+84${phoneNumer.substring(1)}"
        }
        return ""
    }
}