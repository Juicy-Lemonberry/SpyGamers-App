package com.example.spygamers.utils

/**
 * Use this to shorten the content of a text, will replace the last 3 characters of the string with 3 dots (...)
 */
fun shortenText(text: String, maxLength: Int = 45): String {
    return if (text.length > maxLength) {
        "${text.substring(0, maxLength - 3)}..."
    } else {
        text
    }
}