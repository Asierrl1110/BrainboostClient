package com.example.clienteproyectofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import modelo.DTOMazo

class DAOMazo(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "BrainBoostBD.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MAZOS = "Mazos"
        private const val COLUMN_ID = "IdMazo"
        private const val COLUMN_NOMBRE = "Nombre"
        private const val COLUMN_CATEGORIA = "Categoria"
        private const val COLUMN_IDUSUARIO = "IdUsuario"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val execSQL = "CREATE TABLE $TABLE_MAZOS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_NOMBRE TEXT NOT NULL," +
                "$COLUMN_CATEGORIA TEXT NOT NULL," +
                "$COLUMN_IDUSUARIO INTEGER NOT NULL" +
                ")"
        db?.execSQL(execSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MAZOS")
        onCreate(db)
    }

    fun addMazos(listaMazos : List<DTOMazo>, idUsuario : Int){
        val db = writableDatabase
        for(mazo in listaMazos){
            val registro = ContentValues()
            registro.put("IdMazo",mazo.id)
            registro.put("Nombre",mazo.nombre)
            registro.put("Categoria",mazo.categoria)
            registro.put("IdUsuario",idUsuario)
            db.insert(TABLE_MAZOS,null,registro)
        }
    }



    fun deleteMazos(idUsuario: Int){
        val db = writableDatabase
        val sql = "DELETE FROM $TABLE_MAZOS WHERE IdUsuario = $idUsuario"
        db?.execSQL(sql)
    }

}