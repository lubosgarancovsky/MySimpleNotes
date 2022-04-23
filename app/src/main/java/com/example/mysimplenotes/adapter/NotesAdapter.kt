package com.example.mysimplenotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysimplenotes.dataclass.Note
import com.example.mysimplenotes.OnItemClickListener
import com.example.mysimplenotes.R

/**
 * Adapter pre recycklerView poznamok
 * @param mList zoznam poznamok
 * @param listener OnItemClickLister
 *
 * zdroj kodu triedy: https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
 */
class NotesAdapter(private val mList : List<Note>, private val listener : OnItemClickListener) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate xml layoutu pre jednotlive poznamky
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)

        return ViewHolder(view)
    }

    /**
     * Priradi poznamku k view
     * @param holder ViewHolder
     * @param position pozicia poznamky v zozname
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // zapise nazov, text a cas poznamky
        holder.noteHeading.text = itemsViewModel.heading
        holder.noteText.text = itemsViewModel.text
        holder.noteTime.text = itemsViewModel.date
    }

    /**
     * Vracia pocet poznamok v zozname
     * @return pocet poznamok v zozname
     */
    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * vnutorna trieda obsahuje views pre pridavanie textu do poznamky
     * @constructor nastavi OnClickListener
     */
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView), View.OnClickListener {
        val noteHeading : TextView = itemView.findViewById(R.id.note_item_heading)
        val noteText : TextView = itemView.findViewById(R.id.note_item_text)
        val noteTime : TextView = itemView.findViewById(R.id.note_item_time)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position : Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
}