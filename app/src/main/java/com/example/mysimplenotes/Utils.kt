package com.example.mysimplenotes

import java.text.SimpleDateFormat
import java.util.*

/**
 * Trieda so statickymi metodami
 */
class Utils {
    /**
     * Metoda ziskava aktualny datum a cas vo formate dd-MM-yyyy HH:mm:ss
     * @return aktualny datum a cas
     */
    companion object {
        fun getDate() : String{
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }
    }
}