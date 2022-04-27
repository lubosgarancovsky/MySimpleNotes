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
     * @param db DatabaseHandler
     * @param heading EditText s nazvom poznamky
     * @param text EditText s textom poznamky
     * */
    private fun addNote(db : DatabaseHandler, heading : EditText, text : EditText) {
        val note = Note(heading.text.toString(), text.text.toString(), Utils.getDate())

        // ak nazov aj text su prazdne, odide sa z aktivity
        val result = if (heading.text.isEmpty() && text.text.isEmpty()) {
            finish()
            -2

        } else {
            db.insertNote(note)
        }


        // kontroluje, ci sa poznamka pridala, ak ano, odide z aktivity
        when (result) {
            (-1).toLong() -> {Toast.makeText(this, "Note couldn't be created", Toast.LENGTH_SHORT).show()}
            (-2).toLong() -> {finish()}
            else -> {
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Sukromna metoda na aktualizovanie poznamky
     * @param db DatabaseHandler
     * @param oldHeading nazov starej poznamky
     * @param oldText text starej poznamky
     * @param oldTime datum starej poznamky
     * @param newHeading EdiText s novym nazvom poznamky
     * @param newText EditText s novym textom poznamky
     * */
    private fun updateNote(db : DatabaseHandler, oldHeading : CharSequence, oldText : CharSequence, oldTime : CharSequence, newHeading : EditText, newText:EditText ) {

        val note = Note(newHeading.text.toString(), newText.text.toString(), Utils.getDate())

        // ak nazov aj text su prazdne, stara poznamka sa vymaze
        val result: Int = if (newHeading.text.isEmpty() && newText.text.isEmpty()) {
            db.deleteNote(oldHeading, oldText, oldTime)
            finish()
            -1
        } else {
            db.updateNote(oldHeading.toString(), oldText.toString(), oldTime.toString(), note)
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
     * Metoda zmaze aktualne otvorenu poznamku, po kliknuti na tlacidlo zmazat
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

