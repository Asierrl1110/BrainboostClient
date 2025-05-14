package com.example.clienteproyectofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import modelo.DTOTarjeta

class DAOTarjeta(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "BrainBoostBD.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TARJETAS = "Tarjetas"
        private const val COLUMN_IDTARJETA = "IdTarjeta"
        private const val COLUMN_PREGUNTA = "Pregunta"
        private const val COLUMN_RESPUESTA = "Respuesta"
        private const val COLUMN_IDMAZO = "IdMazo"
        private const val TABLE_MAZOS = "Mazos"
        private const val COLUMN_IDUSUARIO = "IdUsuario"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlTarjetas = "CREATE TABLE $TABLE_TARJETAS (" +
                "$COLUMN_IDTARJETA INTEGER PRIMARY KEY," +
                "$COLUMN_PREGUNTA TEXT NOT NULL," +
                "$COLUMN_RESPUESTA TEXT NOT NULL," +
                "$COLUMN_IDMAZO INTEGER NOT NULL" +
                ")"
        db?.execSQL(sqlTarjetas)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TARJETAS")
        onCreate(db)
    }

    fun addTarjetas(listaTarjetas : List<DTOTarjeta>){
        val db = writableDatabase
        for(tarjeta in listaTarjetas){
            val registro = ContentValues()
            registro.put("IdTarjeta",tarjeta.id)
            registro.put("Pregunta",tarjeta.pregunta)
            registro.put("Respuesta",tarjeta.respuesta)
            registro.put("IdMazo",tarjeta.idMazo)
            db.insert(TABLE_TARJETAS,null,registro)
        }
    }

    /*
    fun deleteTarjetas(idUsuario: Int) {
        val db = writableDatabase
        val sql = "DELETE FROM $TABLE_TARJETAS WHERE $COLUMN_IDMAZO IN (" +
                "SELECT $COLUMN_IDMAZO FROM $TABLE_MAZOS WHERE $COLUMN_IDUSUARIO = ?)"
        db.execSQL(sql, arrayOf(idUsuario))
    }
    */

    fun deleteTarjetas(idUsuario: Int) {
        val db = writableDatabase

        // Eliminar tarjetas de los mazos del usuario
        val sqlEliminarPorUsuario = """
        DELETE FROM $TABLE_TARJETAS
        WHERE $COLUMN_IDMAZO IN (
            SELECT $COLUMN_IDMAZO FROM $TABLE_MAZOS WHERE $COLUMN_IDUSUARIO = ?
        )
    """

        // Eliminar tarjetas cuyo mazo ya no existe (mazos huérfanos)
        val sqlEliminarHuerfanas = """
        DELETE FROM $TABLE_TARJETAS
        WHERE $COLUMN_IDMAZO NOT IN (
            SELECT $COLUMN_IDMAZO FROM $TABLE_MAZOS
        )
    """

        db.beginTransaction()
        try {
            db.execSQL(sqlEliminarPorUsuario, arrayOf(idUsuario))
            db.execSQL(sqlEliminarHuerfanas)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }


}