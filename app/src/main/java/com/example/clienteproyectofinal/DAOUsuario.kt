package com.example.clienteproyectofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import modelo.DTOUsuario

class DAOUsuario(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "BrainBoostBD.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "Usuarios"
        private const val COLUMN_IDUSUARIO = "IdUsuario"
        private const val COLUMN_NOMBRE = "Nombre"
        private const val COLUMN_CLAVE = "Clave"
        private const val TABLE_MAZOS = "Mazos"
        private const val COLUMN_IDMAZO = "IdMazo"
        private const val COLUMN_NOMBREMAZO = "Nombre"
        private const val COLUMN_CATEGORIA = "Categoria"
        private const val COLUMN_DESCRIPCION = "Descripcion"
        private const val TABLE_TARJETAS = "Tarjetas"
        private const val COLUMN_IDTARJETA = "IdTarjeta"
        private const val COLUMN_PREGUNTA = "Pregunta"
        private const val COLUMN_RESPUESTA = "Respuesta"
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.execSQL("PRAGMA foreign_keys = ON")
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val sqlUsers = """
        CREATE TABLE $TABLE_USERS (
            $COLUMN_IDUSUARIO INTEGER PRIMARY KEY,
            $COLUMN_NOMBRE TEXT NOT NULL,
            $COLUMN_CLAVE TEXT NOT NULL
        )
    """

        val sqlMazos = """
        CREATE TABLE $TABLE_MAZOS (
            $COLUMN_IDMAZO INTEGER PRIMARY KEY,
            $COLUMN_NOMBREMAZO TEXT NOT NULL,
            $COLUMN_CATEGORIA TEXT NOT NULL,
            $COLUMN_DESCRIPCION TEXT NOT NULL,
            $COLUMN_IDUSUARIO INTEGER NOT NULL,
            FOREIGN KEY($COLUMN_IDUSUARIO) REFERENCES $TABLE_USERS($COLUMN_IDUSUARIO) ON DELETE CASCADE
        )
    """

        val sqlTarjetas = """
        CREATE TABLE $TABLE_TARJETAS (
            $COLUMN_IDTARJETA INTEGER PRIMARY KEY,
            $COLUMN_PREGUNTA TEXT NOT NULL,
            $COLUMN_RESPUESTA TEXT NOT NULL,
            $COLUMN_IDMAZO INTEGER NOT NULL,
            NombreMazo TEXT NOT NULL,
            FOREIGN KEY($COLUMN_IDMAZO) REFERENCES $TABLE_MAZOS($COLUMN_IDMAZO) ON DELETE CASCADE
        )
    """

        db?.execSQL(sqlUsers)
        db?.execSQL(sqlMazos)
        db?.execSQL(sqlTarjetas)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(usuario : DTOUsuario){
        val db = writableDatabase
        val registro = ContentValues()
        registro.put("IdUsuario",usuario.id)
        registro.put("Nombre",usuario.nombreUsuario)
        registro.put("Clave",usuario.clave)
        db.insert(TABLE_USERS,null,registro)
    }

    fun changePassword(clave : String, usuario : DTOUsuario){
        val db = writableDatabase
        val sql = "UPDATE Usuarios SET Clave = ? WHERE IdUsuario = ? AND Nombre = ? AND Clave = ?"
        val args = arrayOf(clave, usuario.id, usuario.nombreUsuario, usuario.clave)

        db.execSQL(sql, args)
    }

    fun deleteUser(usuario: DTOUsuario){
        val db = writableDatabase
        val sql = "DELETE FROM Usuarios WHERE IdUsuario = ? AND Nombre = ? AND Clave = ?"
        val args = arrayOf(usuario.id, usuario.nombreUsuario, usuario.clave)

        db.execSQL(sql,args)
    }

    /*
    fun signup(usuario : DTOUsuario) : Boolean{
        val db = readableDatabase
        var inicio : Boolean = false
        val cursor : Cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_USERS WHERE Nombre = ${usuario.nombre} AND Clave = ${usuario.clave}",null)
        if(cursor.moveToFirst()){
            if(cursor.getInt(0) == 1){
                inicio = true
            }
        }
        return inicio
    }
    */

    fun signup(usuario: DTOUsuario): Boolean {
        val db = readableDatabase
        var inicio = false

        val query = "SELECT * FROM $TABLE_USERS WHERE Nombre = ? AND Clave = ?"
        val cursor = db.rawQuery(query, arrayOf(usuario.nombreUsuario, usuario.clave))

        if (cursor.moveToFirst()) {
            inicio = true
            val usuario = DTOUsuario(cursor.getInt(0),cursor.getString(1),cursor.getString(2))
            ZonaCompartida.setUsuarioRegistrado(usuario)
        }
        cursor.close()
        return inicio
    }

}