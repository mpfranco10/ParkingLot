package com.example.parkinglot

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ParqueaderoOpenHelper (context: Context,
                             factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME
                + " TEXT," +
                COLUMN_IMG
                + " TEXT," +
                COLUMN_PRECIO
                + " TEXT," +
                COLUMN_HORARIO
                + " TEXT" + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }
    fun addName(name: Parqueadero) {
        val values = ContentValues()
        values.put(COLUMN_NAME, name.userName)
        values.put(COLUMN_IMG, name.imagen)
        values.put(COLUMN_PRECIO, name.precio)
        values.put(COLUMN_HORARIO, name.horario)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllName(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ParkingLot.db"
        val TABLE_NAME = "Parqueaderos"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "username"
        val COLUMN_IMG = "imagen"
        val COLUMN_PRECIO = "precio"
        val COLUMN_HORARIO = "horario"
    }
}