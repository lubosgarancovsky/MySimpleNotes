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

    //globalny atribut, ktory rozhoduje ci sa jedna o novu poznamku alebo exitujuci poznamku
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

        //Nadpis poznamky nesmie byt prazdny
        if (heading.text.toString().isNotEmpty()) {
            //ak pouzivatel vytvara novu poznamku
            if (isNew) {

                //vytvori sa nova poznamka
                val note = Note(heading.text.toString(), text.text.toString(), Utils.getDate())

                //vracia -1 ak sa nepodarilo vykonat insert query
                val result : Long = db.insertNote(note)

                //ak sa nepdarilo vykonat insert query, nepohne sa na predchadzajucu aktivitu
                if (result == (-1).toLong()){
                    Toast.makeText(this, "Note couldn't be created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                    //ak bola poznamka pridana, moze sa pohnut na predchadzajucu aktivitu (finnish zavola onDestroy aktivity)
                    finish()
                }

            //ak sa upravuje existujuca poznamka
            } else {
                //nova poznamka
                val note = Note(heading.text.toString(), text.text.toString(), Utils.getDate())

                //poznamka sa updatuje iba ak bola zmenena
                //tj. stare udaje sa nerovnaju novym
                if (note.heading != noteHeading || note.text != noteText) {
                    //vracia pocet aktualizovanych zaznamov v DB
                    val result = db.updateNote(noteHeading.toString(), noteText.toString(), noteTime.toString(), note)

                    //kotrluje pocet updatovanych zaznamov
                    if(result > 0) {
                        //update query presiel a poznamka sa aktualizovala, dostava sa na predchadzajuci aktivitu
                        Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else {
                        //update query nepresiel,ostava sa na aktivite
                        Toast.makeText(this, "Note not updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            finish()
        } else {
            //ak je pole pre nazov poznamky prazdne, iba sa vyvola toast message
            Toast.makeText(this, "Header cant be empty", Toast.LENGTH_SHORT).show()
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

