package com.example.mysimplenotes.dataclass

/**
 * data class pre poznamku, ktora uklada jej nazov, obsah a datum vytvorenia
 * @param heading nadpis poznamky
 * @param text obsah poznamky
 * @param date datum vytvorenia poznamky
 */

data class Note (var heading:String, var text:String, var date:String)
