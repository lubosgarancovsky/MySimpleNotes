package com.example.mysimplenotes.dataclass

/**
 * data class pre ulohu, ktora uklada jej nazov, prioritu a datum vytvorenia
 * @param text text/nazov ulohy
 * @param priority priorita ulohy (0, 1, 2) -> (low, mid, high)
 * @param date datum pridania ulohy
 */
data class Task (var text : String, var priority : Int, var date : String)