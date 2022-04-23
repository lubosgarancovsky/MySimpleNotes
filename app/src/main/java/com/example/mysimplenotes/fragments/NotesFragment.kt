package com.example.mysimplenotes.fragments

import com.example.mysimplenotes.adapter.NotesAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysimplenotes.*
import com.example.mysimplenotes.activities.AddNoteActivity
import com.example.mysimplenotes.dataclass.Note

/**
 * Fragment so zoznamom vytvorenych poznamok
 * Obsahuje RecyclerView, tlacidlo na pridavanie novej poznamky
 */
class NotesFragment : Fragment(), OnItemClickListener {

    //ArrayList poznamok, ktore sa na fragmente zobrazuju
    private var notesList : ArrayList<Note> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //skryje action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //tlacidlo, ktore otvara aktivitu pre pridanie, upravenie, vymazanie poznamky
        val addNoteButton : Button = view.findViewById(R.id.add_button)

        //aktivita na ktorej sa da vytvorit nova alebo upravit\vymazat stara poznamka
        val addNoteActivity = Intent (activity, AddNoteActivity::class.java)


        //listener pre addNoteButton tlacidlo otvori "AddNoteActivity" aktivitu
        addNoteButton.setOnClickListener() {
            addNoteActivity.putExtra("newNote", true)
            startActivity(addNoteActivity)
        }
    }

    override fun onResume() {
        super.onResume()

        val context : Context? = context
        val db = DatabaseHandler(context)

        //do ArrayListu nacita vsetky poznamky z databazy
        notesList = db.readNotes()

        //vola metodu showNotes, ktora vypise poznamky do recyclerView
        if (context != null) {
            showNotes(context, notesList)
        }
    }

    /**
     * Metoda sluzi na vypisanie poznamok v recyclerView
     * @param context
     * @param notesList zoznam poznamok
     */
    private fun showNotes (context: Context, notesList : ArrayList<Note>) {
        // zisakva recclerView
        val recyclerview : RecyclerView =  requireView().findViewById(R.id.notesRecycler)

        // vytvori vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)


        // Posunie zoznam poznamok do adaptera
        val adapter = NotesAdapter(notesList, this)

        recyclerview.adapter = adapter

        //cast kodu prevzata z:
        //https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
    }

    /**
     * Metoda otvori novu aktivitu, ktorej posle atributy poznamky, na ktoru sa kliklo v recyclerView
     * @param position pozicia kliknutej poznamky v recyclerView
     */
    override fun onItemClick(position: Int) {
        //ziska poznamku, na ktoru sa kliklo
        val clickedItem : Note = notesList[position]

        //vyvola aktivitu na pridavanie poznamky, kde posle atributy prave otvorenej poznamky
        val addNoteActivity = Intent (activity, AddNoteActivity::class.java)

        //posuvanie atributov do aktivity
        addNoteActivity.putExtra("heading", clickedItem.heading)
        addNoteActivity.putExtra("text", clickedItem.text)
        addNoteActivity.putExtra("time", clickedItem.date)
        addNoteActivity.putExtra("newNote", false)

        //zavola aktivitu
        startActivity(addNoteActivity)
    }
}
