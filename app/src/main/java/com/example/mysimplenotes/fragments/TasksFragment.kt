package com.example.mysimplenotes.fragments

import com.example.mysimplenotes.adapter.TasksAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysimplenotes.*
import com.example.mysimplenotes.activities.AddTaskActivity
import com.example.mysimplenotes.dataclass.Task

/**
 * Fragment, na ktorom sa zobrazuju ulohy
 */
class TasksFragment : Fragment(), OnItemClickListener {

    //ArrayList uloh
    private var tasksList : ArrayList<Task> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //skryje action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //tlacidlo pre pridavanie novej ulohy
        val addTaskButton : Button = view.findViewById(R.id.add_button)

        //intent pre aktivitu na pridavanie uloh
        val addTaskActivity = Intent(activity, AddTaskActivity::class.java)

        //listener pre tlacidlo ktore otvori oktivitu na pridanei novej ulohy
        addTaskButton.setOnClickListener {
            startActivity(addTaskActivity)
        }

    }

    override fun onResume() {
        super.onResume()

        val context : Context? = context
        val db = DatabaseHandler(context)

        //precita ArrayList uloh z databazy
        tasksList = db.readTasks()


        //volanie funkcie pre vypis uloh do recyclerView
        if (context != null) {
            showTasks(context, tasksList)
        }
    }


    /**
     * Metoda vypisuje ulohy do recyclerView
     * @param context
     * @param tasksList zoznam uloh
     */
    private fun showTasks (context: Context, tasksList : ArrayList<Task>) {
        // zisakva recclerView
        val recyclerview : RecyclerView =  requireView().findViewById(R.id.tasksRecycler)

        // vytvori vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)

        // Posunie zoznam poznamok do adaptera
        val adapter = TasksAdapter(tasksList, this)

        recyclerview.adapter = adapter

        //cast kodu prevzata z:
        //https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
    }

    /**
     * Metoda otvori novu aktivitu, ktorej posle atributy ulohy, na ktoru sa kliklo v recyclerView
     * @param position pozicia kliknutej ulohy v recyclerView
     */
    override fun onItemClick(position: Int) {
        //ziska poziciu ulohy, na ktoru sa kliklo
        val clickedItem : Task = tasksList[position]

        //intent aktivity na pridavanie ulohy
        val addTaskActivity = Intent (activity, AddTaskActivity::class.java)

        //posuvanie atributov do aktivity
        addTaskActivity.putExtra("priority", clickedItem.priority)
        addTaskActivity.putExtra("task", clickedItem.text)
        addTaskActivity.putExtra("date", clickedItem.date)
        addTaskActivity.putExtra("newTask", false)

        //zavola aktivitu
        startActivity(addTaskActivity)

    }


}