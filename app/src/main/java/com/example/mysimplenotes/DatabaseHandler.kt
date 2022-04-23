package com.example.mysimplenotes

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mysimplenotes.dataclass.Note
import com.example.mysimplenotes.dataclass.Task

const val DB_NAME = "SimpleNoteDB"

/**
 * Tato trieda sa stara o SQLite databazu
 * Vytvara tabulky, vklada, vypisuje a maze zaznamy
 */
class DatabaseHandler(var context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {

        //SQL kod pre vytvorenie tabulky poznamok
        val createTableNotes = "CREATE TABLE Notes (id INTEGER PRIMARY KEY AUTOINCREMENT, heading VARCHAR(20), text TEXT, date DATE)"

        //SQL kod pre vytvorenie tabulky uloh
        val createTableTasks = "CREATE TABLE Tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, priority INTEGER, task VARCHAR(50), date DATE)"

        //Exec SQL kodu
        p0?.execSQL(createTableNotes)
        p0?.execSQL(createTableTasks)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Metoda vklada poznamku do databazy
     * @param note Instancia triedy note, teda poznamky ktora obsahuje nadpis a text
     * @return index riadku, (-1) ak sa insert query nepodarilo
     */
    fun insertNote(note : Note) : Long{

        // ziskava DB pre zapis
        val db = this.writableDatabase

        // cv je set hodnot poznamky
        val cv = ContentValues()

        // do cv sa vkladaju hodnotu poznamky, ktora prisla ako parameter metody
        cv.put("heading", note.heading)
        cv.put("text", note.text)
        cv.put("date", note.date)

        // insert query vracia index vlozeneho zaznamu alebo -1 ak query zlyhalo
        // parametrami insertu je tabulka, nullColumnHack, a ContentValue s hodnotami vkladanej poznamky
        val result = db.insert("Notes", null, cv)

        //zatvara sa DB a vracia sa hodnota result
        db.close()
        return result
    }

    /**
     * Metoda posiela do databazy update query
     * Upravuje uz vytvorenu poznamku
     *
     * @param header nazov starej poznamky
     * @param text text starej poznamky
     * @param date datum starej poznamky
     * @param note nova poznamka, ktorou sa nahradza ta stara
     * @return pocet upravenych zaznamov
     */
    fun updateNote(header : String, text : String, date : String, note: Note) : Int{
        val db = this.readableDatabase
        val cv = ContentValues()

        cv.put("heading", note.heading)
        cv.put("text", note.text)
        cv.put("date", note.date)

        // update(Tabulka, vkladanie hodnoty, podmineka where, parametre pre podmienku where)
        // parametre pre podmienku where su atributy starej poznamky, ktore prisli ako parametre metody
        // vkladane hodnoty su parametre novej poznamky
        val result = db.update("Notes", cv, "heading = ? and text = ? and date = ?", arrayOf(header, text, date))
        db.close()

        //vrati pocet upravenych zaznamov
        return result
    }

    /**
     * Metoda vymazava poznamky z databazy na zaklade vstupnych parametrov header, text a date starej poznamky
     *
     * @param header hlavicka poznamky ktora sa ma vymazat
     * @param text text poznamky ktora sa ma vymazat
     * @param date datum poznamky, ktora sa ma vymazat
     * @return pocet zmazanych zaznamov
     */
    fun deleteNote(header : CharSequence, text : CharSequence, date : CharSequence) : Int {
        val db = this.writableDatabase

        // delte(tabulka, podmineka, parametre podmienky)
        // zmaze poznamku ktorej hodnoty prisli ako parametre metody
        val result : Int = db.delete("Notes", "heading=? and text=? and date = ?", arrayOf("$header", "$text", "$date"))
        db.close()

        //vrati pocet zmazanych zaznamov
        return result
    }


    /**
     * Metoda posiela SELECT query do databazy
     * Vracia pole poznamok, ktore vybrala z DB
     *
     * @return notesList -> pole poznamok v databaze typu ArrayList<Note>
     */
    @SuppressLint("Range")
    fun readNotes() : ArrayList<Note>{
        val db = this.readableDatabase
        val selectQry = "SELECT * FROM Notes ORDER BY id DESC"

        //uklada zaznamy v selecte ziskane pomocou rawQuery
        val cursor : Cursor = db.rawQuery(selectQry, null)

        //inicializacia zoznamu poznamok
        val notesList : ArrayList<Note> = ArrayList()

        //parametre poznamky
        var heading : String
        var text : String
        var date : String

        //pre kazdy zaznam sa ziska nadpis, text a datum poznamky a ulozia do premennych, ktore sluzia ako parametre pre novu poznamku
        while(cursor.moveToNext()) {
            // 1,2,3 znamenaju index stlpca v tabulke
            heading = cursor.getString(1)
            text = cursor.getString(2)
            date = cursor.getString(3)

            //do zoznamu sa prida nova poznamka s parametrami ziskanymi z DB
            notesList.add(Note(heading, text, date))
        }


        //zatvara DB a aj cursor
        db.close()
        cursor.close()

        //vracia zoznam poznamok
        return notesList
    }


    /**
     * Metoda posiela SELECT query do databazy
     * Vracia pole uloh, ktore vybrala z DB
     *
     * @return tasksList -> pole uloh v databaze typu ArrayList<Task>
     */
    fun  readTasks() :ArrayList<Task> {
        val db = this.readableDatabase

        //query pre ulohu
        val selectQry = "SELECT * FROM Tasks ORDER BY priority DESC , date DESC"

        val cursor : Cursor = db.rawQuery(selectQry, null)

        // zoznam uloh
        val tasksList : ArrayList<Task> = ArrayList()

        // parametre ziskanej ulohy
        var text : String
        var priority : Int
        var date : String

        while(cursor.moveToNext()) {
            priority = cursor.getInt(1)
            text = cursor.getString(2)
            date = cursor.getString(3)

            tasksList.add(Task(text, priority, date))
        }


        db.close()
        cursor.close()


        //vracia pole uloh ziskanych zo selectu
        return tasksList
    }

    /**
     * Metoda pre vkladanie ulohy do DB
     * @param task Uloha, ktora sa ma vlozit
     * @return index vlozeneho riadka (-1) ak sa riadok vlozit nepodarilo
     */
    fun insertTask(task : Task) : Long{
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put("priority", task.priority)
        cv.put("task", task.text)
        cv.put("date", task.date)

        val result = db.insert("Tasks", null, cv)

        db.close()
        return result
    }

    /**
     * Metoda pre upravu existujucej ulohy
     * @param text text starej ulohy
     * @param priority priorita strej ulohy
     * @param date datum strej ulohy
     * @param task objekt novej ulohy
     * @return pocet upravenych zaznamov
     */
    fun updateTask(text : String, priority : Int, date : String, task : Task) : Int {
        val db = this.readableDatabase
        val cv = ContentValues()

        cv.put("priority", task.priority)
        cv.put("task", task.text)
        cv.put("date", task.date)

        val result = db.update("Tasks", cv, "task = ? and priority = ? and date = ?", arrayOf(text,priority.toString(), date))
        db.close()
        return result
    }


    /**
     * Metoda pre zmazanie ulohy
     * parametre sa vkladaju do sql delete query
     * @param text text ulohy
     * @param priority priorita ulohy
     * @param date datum pridania ulohy
     */
    fun deleteTask(text : String, priority : Int, date : String) : Int{
        val db = this.writableDatabase
        val result : Int = db.delete("Tasks", "task=? and priority=? and date = ?", arrayOf(text, priority.toString(), date))
        db.close()

        return result
    }

}
