package com.example.mysimplenotes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.mysimplenotes.DatabaseHandler
import com.example.mysimplenotes.Utils
import com.example.mysimplenotes.R
import com.example.mysimplenotes.dataclass.Task
import com.google.android.material.textfield.TextInputEditText

class AddTaskActivity : AppCompatActivity() {

    //uloha moze byt nova (true), alebo sa otvara uz existujuca (false)
    private var isNew : Boolean = true

    //atributy otvorenej ulohy
    private var task : CharSequence = ""
    private var date : CharSequence = ""
    private var priority : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        //zobrazi tlacidlo spat na actionbare
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //tlacidlo na zmazanie ulohy
        val deleteBtn : Button = findViewById(R.id.task_delete_btn)

        // ziskava intent a atribut newTask, cize zistuje z predchadzajucej aktivity, ci sa jedna o novu ulohu alebo nie
        val intent = intent
        val newTask = intent.getBooleanExtra("newTask", true)

        //ak je uloha nova, atributy otvorenej ulohy ostavaju prazdne
        if (newTask) {
            isNew = true
        }
        //ak sa otvorila existujuca uloha, nastavia sa jej atributy
        else {
            isNew = false
            task = intent.getStringExtra("task").toString()
            date = intent.getStringExtra("date").toString()
            priority = intent.getIntExtra("priority", 0)

            //vlozi text ulohy do prislusneho EditText pola
            findViewById<EditText>(R.id.task_input).setText(task)

            //ziskava prioritu poznamky zo zakliknuteho radiobuttonu
            when (priority) {
                 0 -> findViewById<RadioButton>(R.id.low_priority).isChecked = true
                 1 -> findViewById<RadioButton>(R.id.medium_priority).isChecked = true
                 2 -> findViewById<RadioButton>(R.id.high_priority).isChecked = true
             }
        }


        //delete tlacidlo vola metodu na zmazanie ulohy
        deleteBtn.setOnClickListener() {
            tryDeleteTask()
        }
    }

    /**
     * Metoda sa zavola po stlaceni tlacidla spat
     */
    override fun onBackPressed() {
        // handler databazy
        val db = DatabaseHandler(this)

        // pole pre text ulohy
        val taskTextInput : TextInputEditText= findViewById(R.id.task_input)

        // text ulohy nesmie byt prazdny
        if (taskTextInput.text.toString().isNotEmpty() && getPriority() != -1) {

            // ak sa jedna o novu poznamku
            if(isNew) {
                // v resulte je vysledok insert query (rowId ak sa insert podaril, -1 ak sa nepodaril)
                val result = db.insertTask(Task(taskTextInput.text.toString(), getPriority(), Utils.getDate()))

                // vypise sa vysledok insert query ako toas message
                if (result == (-1).toLong()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                    //ukonci aktivitu
                    finish()
                }

            // ak sa upravuje/maze stara poznamka
            } else {

                // instancia novez poznamky, ziskava text z pola pre text na aktivite, prioritu zo zakliknuteho radiobuttonua stary datum
                // stary datum preto, lebo pri upravovani ulohy nechcem menit jej datum upravy tak ako pri poznamke
                val newTask = Task(taskTextInput.text.toString(), getPriority(), date.toString())

                //ak sa text/priorita/datum ulohy zmenil, vola sa update databazy
                if (newTask.text != task || newTask.priority != priority || newTask.date != date) {

                    //query vracia pocet aktualizovanych zaznamov v DB
                    val result = db.updateTask(task.toString(), priority, date.toString(), newTask)

                    if(result > 0) {
                        //update query presiel a poznamka sa aktualizovala, dostava sa na predchadzajuci aktivitu
                        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else {
                        //update query nepresiel,ostava sa na aktivite
                        Toast.makeText(this, "Task not updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            //este neboli vyplnene vsetky polia
            Toast.makeText(this, "FIll in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Ziska prioritu ulohy zo zakliknuteho radioButtonu
     * (0, 1, 2) -> (low, meidum, high)
     * @return priorita ulohy
     */
    private fun getPriority() : Int{
        return when {
            findViewById<RadioButton>(R.id.low_priority).isChecked -> 0
            findViewById<RadioButton>(R.id.medium_priority).isChecked -> 1
            findViewById<RadioButton>(R.id.high_priority).isChecked -> 2
            else -> return -1
        }
    }

    /**
     * Metoda vola query pre mazanie zaznamu z DB
     */
    private fun tryDeleteTask() {
        var db = DatabaseHandler(this)

        // vysledkom query pre zmazanie ulohy je pocet vymazanych zaznamov
        // parametrami pre delete query je stara uloha (jej atributy)
        val result = db.deleteTask(task.toString(), priority, date.toString())

        // ak sa uloha zmazala, vypise sa toast
        if (result > 0) {
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
        }

        //ukoncuje aktivitu
        finish()
    }

    /**
     * Metoda zaistuje ze tlacidlo spat na actionbare ukonci aktivitu, vrati sa na task fragment
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

}