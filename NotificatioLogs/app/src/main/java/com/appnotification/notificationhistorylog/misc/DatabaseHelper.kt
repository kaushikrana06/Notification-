package com.appnotification.notificationhistorylog.misc

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES_POSTED)
        db.execSQL(SQL_CREATE_ENTRIES_REMOVED)
    }

    // Implementation
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES_POSTED)
        db.execSQL(SQL_DELETE_ENTRIES_REMOVED)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    object PostedEntry : BaseColumns {
        const val TABLE_NAME = "notifications_posted"
        const val COLUMN_NAME_CONTENT = "content"
        const val COLUMN_NAME_FAVORITE = "favorite"
    }

    object RemovedEntry : BaseColumns {
        const val TABLE_NAME = "notifications_removed"
        const val COLUMN_NAME_CONTENT = "content"
    }

    companion object {
        const val SQL_CREATE_ENTRIES_POSTED = "CREATE TABLE " + PostedEntry.TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY," +
                PostedEntry.COLUMN_NAME_CONTENT + " TEXT," +
                PostedEntry.COLUMN_NAME_FAVORITE + " INTEGER DEFAULT 0)"
        const val SQL_DELETE_ENTRIES_POSTED = "DROP TABLE IF EXISTS " + PostedEntry.TABLE_NAME

        // Posted notifications
        const val SQL_CREATE_ENTRIES_REMOVED = "CREATE TABLE " + RemovedEntry.TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY," +
                RemovedEntry.COLUMN_NAME_CONTENT + " TEXT)"
        const val SQL_DELETE_ENTRIES_REMOVED = "DROP TABLE IF EXISTS " + RemovedEntry.TABLE_NAME
        private const val DATABASE_VERSION = 1

        // Removed notifications
        private const val DATABASE_NAME = "notifications.db"
    }
}