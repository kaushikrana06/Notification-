package com.appnotification.notificationhistorylog.db

import android.database.sqlite.SQLiteOpenHelper
import com.appnotification.notificationhistorylog.db.DbHelperGmail
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import java.util.ArrayList

class DbHelperGmail(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VER) {
    override fun onCreate(db: SQLiteDatabase) {
        val query = String.format(
            "CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL);",
            DB_TABLE,
            DB_COLUMN
        )
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val query = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE)
        db.execSQL(query)
        onCreate(db)
    }

    fun insertNewTask(task: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DB_COLUMN, task)
        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun deleteTask(task: String) {
        val db = this.writableDatabase
        db.delete(DB_TABLE, DB_COLUMN + " = ?", arrayOf(task))
        db.close()
    }

    val taskList: ArrayList<String>
        get() {
            val taskList = ArrayList<String>()
            val db = this.readableDatabase
            val cursor = db.query(DB_TABLE, arrayOf(DB_COLUMN), null, null, null, null, null)
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(DB_COLUMN)
                taskList.add(cursor.getString(index))
            }
            cursor.close()
            db.close()
            return taskList
        }

    companion object {
        const val DB_TABLE = "Taskgmail"
        const val DB_COLUMN = "tasknamegmail"
        private const val DB_NAME = "GmailLogs"
        private const val DB_VER = 2
    }
}