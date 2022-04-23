package com.example.mysimplenotes

/**
 * Interface s metodou pre riesenie klikania na item v recyclerView
 */
interface OnItemClickListener {

    /**
     * Metoda ktora sa vola po kliknuti na item v recyclerView
     * @param position pozicia kliknuteho itemu
     */
    fun onItemClick(position : Int)
}