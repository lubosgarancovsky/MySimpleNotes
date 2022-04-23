package com.example.mysimplenotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysimplenotes.OnItemClickListener
import com.example.mysimplenotes.R
import com.example.mysimplenotes.dataclass.Task

/**
 * Adapter pre recycklerView poznamok
 * @param mList zoznam uloh
 * @param listener OnItemClickLister
 *
 * zdroj kodu triedy: https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
 */
class TasksAdapter(private val mList: List<Task>, private val listener : OnItemClickListener) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate card view layoutu note_item, ktory ma v sebe ulohy
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)

        return ViewHolder(view)
    }

    /**
     * Priradi ulohu k view
     * @param holder ViewHolder
     * @param position pozicia poznamky v zozname
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.taskText.text = itemsViewModel.text
        holder.taskDate.text = itemsViewModel.date
        when (mList[position].priority){
            0 -> holder.taskPriority.setImageResource(R.drawable.low_priority_img)
            1 -> holder.taskPriority.setImageResource(R.drawable.medium_priority_img)
            else -> holder.taskPriority.setImageResource(R.drawable.high_priority_img)
        }


    }

    /**
     * Vracia pocet ulohv zozname
     * @return pocet uloh v zozname
     */
    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * vnutorna trieda obsahuje views pre pridavanie textu do poznamky
     * @constructor nastavi OnClickListener
     */
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView), View.OnClickListener {
        val taskText: TextView = itemView.findViewById(R.id.task_item_text)
        val taskPriority : ImageView = itemView.findViewById(R.id.task_priority_image)
        val taskDate : TextView = itemView.findViewById(R.id.task_item_date)

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