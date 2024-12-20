package com.riga.expenc

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CATEGORIA)
        db.execSQL(CREATE_TABLE_GASTO)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GASTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIA")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "expenses.db"
        const val DATABASE_VERSION = 1

        const val TABLE_CATEGORIA = "categoria"
        const val COLUMN_CATEGORIA_ID = "id"
        const val COLUMN_CATEGORIA_NOMBRE = "nombre"

        const val TABLE_GASTO = "gasto"
        const val COLUMN_GASTO_ID = "id"
        const val COLUMN_GASTO_NOMBRE = "nombre"
        const val COLUMN_GASTO_MONTO = "monto"
        const val COLUMN_GASTO_FECHA = "fecha"
        const val COLUMN_GASTO_CATEGORIA_ID = "categoria_id"

        private const val CREATE_TABLE_CATEGORIA = (
                "CREATE TABLE $TABLE_CATEGORIA (" +
                        "$COLUMN_CATEGORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_CATEGORIA_NOMBRE TEXT NOT NULL)"
                )

        const val INSERT_CATEGORIA = (
                "INSERT INTO $TABLE_CATEGORIA ($COLUMN_CATEGORIA_NOMBRE) VALUES ('Ejemplo')"
                )

        private const val CREATE_TABLE_GASTO = (
                "CREATE TABLE $TABLE_GASTO (" +
                        "$COLUMN_GASTO_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_GASTO_NOMBRE TEXT NOT NULL, " +
                        "$COLUMN_GASTO_MONTO REAL NOT NULL, " +
                        "$COLUMN_GASTO_FECHA TEXT NOT NULL, " +
                        "$COLUMN_GASTO_CATEGORIA_ID INTEGER, " +
                        "FOREIGN KEY($COLUMN_GASTO_CATEGORIA_ID) REFERENCES $TABLE_CATEGORIA($COLUMN_CATEGORIA_ID))"
                )
    }
}