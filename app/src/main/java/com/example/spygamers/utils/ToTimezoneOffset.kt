package com.example.spygamers.utils

/**
 * https://www.ibm.com/docs/en/cics-ts/5.5?topic=definition-time-zone-codes
 *
 * @return If `A`, returns 'None Set'. If bad code, returns 'Invalid Code'
 */
fun toTimezoneOffset(code: String): String {
    return when (code.uppercase()) {
        "A" -> "None Set"
        "B" -> "GMT +1"
        "C" -> "GMT +2"
        "D" -> "GMT +3"
        "E" -> "GMT +4"
        "F" -> "GMT +5"
        "G" -> "GMT +6"
        "H" -> "GMT +7"
        "I" -> "GMT +8"
        "J" -> "GMT +9"
        "K" -> "GMT +10"
        "L" -> "GMT +11"
        "M" -> "GMT +12"
        "N" -> "GMT -1"
        "O" -> "GMT -2"
        "P" -> "GMT -3"
        "Q" -> "GMT -4"
        "R" -> "GMT -5"
        "S" -> "GMT -6"
        "T" -> "GMT -7"
        "U" -> "GMT -8"
        "V" -> "GMT -9"
        "W" -> "GMT -10"
        "X" -> "GMT -11"
        "Y" -> "GMT -12"
        "Z" -> "GMT 0"
        else -> "Invalid code"
    }
}