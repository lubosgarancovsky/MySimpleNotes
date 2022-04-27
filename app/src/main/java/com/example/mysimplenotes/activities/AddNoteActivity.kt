package com.example.mysimplenotes.activities


import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mysimplenotes.DatabaseHandler
import com.example.mysimplenotes.Utils
import com.example.mysimplenotes.dataclass.Note
import com.example.mysimplenotes.R

class AddNoteActivity : AppCompatActivity() {

    //atribut, ktory rozhoduje ci sa jedna o novu poznamku alebo exitujuci poznamku
    private var isNew : Boolean = true

    //atributy existujucej poznamky
    private var noteHeading : CharSequence = ""
    private var noteText : CharSequence = ""
    private var noteTime : CharSequence = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        //zobrazi tlacidlo spat na actionbare
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //tlacidlo pre zmazanie poznamky
        val deleteBtn : Button = findViewById(R.id.note_delete_btn)

        //ziskava intent a atribut newNote, aby aktivita vedela, ci sa vytvara nova poznamka alebo upravuje existujuca
        val intent = intent
        val newNote = intent.getBooleanExtra("newNote", true)

        //podla toho sa nastavi globalny atribut isNew
        if (newNote) {
            isNew = true
        }
        else {
            //ak sa jedna o existujuci poznamku, ziskaju hodnoty jej parametrov
            isNew = false
            noteHeading = intent.getStringExtra("heading").toString()
            noteText = intent.getStringExtra("text").toString()
            noteTime = intent.getStringExtra("time").toString()

            //prednastavi text v polickach podla otvorenej poznamky
            findViewById<EditText>(R.id.note_heading).setText(noteHeading)
            findViewById<EditText>(R.id.note_text).setText(noteText)
        }

        //tlacidlo pre mazanie poznamky
        deleteBtn.setOnClickListener {
            tryDeleteNote()
        }
    }

    /**
     * Metoda zaistuje ze po stlaceni tlacidla spat na telefone sa bud vytvori alebo upravi poznamka
     * Ak pole s nadpisom poznamky je prazdne, z aktivity sa neodchadza a vypise sa toast message
     */
    override fun onBackPressed() {
        val db = DatabaseHandler(this)

        //najde polia pre hlavicku a text
        val heading = findViewById<EditText>(R.id.note_heading)
        val text = findViewById<EditText>(R.id.note_text)

        if (isNew) {
            addNote(db, heading, text)
        }
        else {
            updateNote(db, noteHeading, noteText, noteTime, heading, text)
        }
    }

    /**
     * Sukromna metoda na pridavanie novej poznamky
     * */
    private fun addNote(db : DatabaseHandler, heading : EditText, text : EditText) {
        var note : Note
        var result : Long = -1

        // ak nazov aj text su prazdne, odide sa z aktivity
        if (heading.text.isEmpty() && text.text.isEmpty()) {
            finish()
        }

        //ak nazov poznamky je prazdny, ale text nie, text sa pouzije ako nadpis
        if (heading.text.isEmpty() && text.text.isNotEmpty()) {
            note = Note(text.text.toString(), text.text.toString(), Utils.getDate())
            result = db.insertNote(note)
        }

        // ak nazov poznamke nie je prazdy ale text je prazdny, prida sa poznamka bez textu
        // ak nazov ani text poznamky nie su prazdne, prid sa normalna poznamka
        if (heading.text.isNotEmpty()) {
            if (text.text.isNotEmpty()) {
                note = Note(heading.text.toString(), text.text.toString(), Utils.getDate())
                result = db.insertNote(note)
            } else {
                note = Note(heading.text.toString(), "", Utils.getDate())
                result = db.insertNote(note)
            }
        }

        // kontroluje, ci sa poznamka pridala, ak ano, odide z aktivity
        if (result == (-1).toLong()) {
            Toast.makeText(this, "Note couldn't be created", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateNote(db : DatabaseHandler, oldHeading : CharSequence, oldText : CharSequence, oldTime : CharSequence, newHeading : EditText, newText:EditText ) {
        var note : Note
        var result: Int

        // ak nazov aj text su prazdne, stara poznamka sa vymaze
        if (newHeading.text.isEmpty() && newText.text.isEmpty()) {
            db.deleteNote(oldHeading, oldText, oldTime)
            finish()
            result = -1
        }
        //ak nazov poznamky je prazdny, ale text nie, text sa pouzije ako nadpis
        else if (newHeading.text.isEmpty() && newText.text.isNotEmpty()) {
            note = Note(newText.text.toString(), newText.text.toString(), Utils.getDate())
            result = db.updateNote(oldHeading.toString(), oldText.toString(), oldTime.toString(), note)
        }
        else {
            note = Note(newHeading.text.toString(), newText.text.toString(), Utils.getDate())
            result = db.updateNote(oldHeading.toString(), oldText.toString(), oldTime.toString(), note)
        }

        // kontroluje, ci sa poznamka aktualizovala alebo vymazala, ak ano, odide z aktivity
        when (result) {
            -1  -> Toast.makeText(this, "Note was deleted", Toast.LENGTH_SHORT).show()
             0 -> Toast.makeText(this, "Note couldn't be updated", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }



    /**
     * Metoda zmaze poznamku, po kliknuti na tlacidlo zmazat
     */
    private fun tryDeleteNote() {
        //posiela DB handleru spravu na zmazanie poznamky
        // parametre su obsach atributov starej poznamky, ktore idu do podmienky v SQL query
        val db = DatabaseHandler(this)

        //result je pocet vymazanych riadkov v databaze
        val result = db.deleteNote(noteHeading, noteText, noteTime)

        //ak sa vymazal nejaky riadok, vypise sa toast
        if (result > 0) {
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
        }

        //ukoncenie aktivity
        finish()
    }

    /**
     * Metoda zaistuje ze tlacidlo spat na actionbare ukonci aktivitu, vrati sa na note fragment
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

}

