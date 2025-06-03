package com.example.clienteproyectofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import modelo.DTOUsuario
import java.text.SimpleDateFormat
import java.util.Locale

class DAOUsuario(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "BrainBoostBD.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "Usuarios"
        private const val COLUMN_IDUSUARIO = "IdUsuario"
        private const val COLUMN_NOMBREUSUARIO = "NombreUsuario"
        private const val COLUMN_CLAVE = "Clave"
        private const val COLUMN_NOMBRE = "Nombre"
        private const val COLUMN_APELLIDOS = "Apellidos"
        private const val COLUMN_FECHANACIMIENTO = "FechaNacimiento"
        private const val COLUMN_GENERO = "Genero"
        private const val COLUMN_ROL = "Rol"
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
            $COLUMN_NOMBREUSUARIO TEXT NOT NULL,
            $COLUMN_CLAVE TEXT NOT NULL,
            $COLUMN_NOMBRE TEXT,
            $COLUMN_APELLIDOS TEXT,
            $COLUMN_FECHANACIMIENTO TEXT,
            $COLUMN_GENERO TEXT,
            $COLUMN_ROL TEXT
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
        registro.put("NombreUsuario",usuario.nombreUsuario)
        registro.put("Clave",usuario.clave)
        registro.put("Nombre",usuario.nombre)
        registro.put("Apellidos",usuario.apellidos)
        // Guardar la fecha como String en formato yyyy-MM-dd
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val fechaStr = sdf.format(usuario.fechaNacimiento)
        registro.put("FechaNacimiento", fechaStr)
        //registro.put("FechaNacimiento","20/02/2020")
        registro.put("Genero",usuario.genero)
        registro.put("Rol",usuario.rol)
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

    fun signup(usuario: DTOUsuario): Boolean {
        val db = readableDatabase
        var inicio = false

        val query = "SELECT * FROM $TABLE_USERS WHERE NombreUsuario = ? AND Clave = ?"
        val cursor = db.rawQuery(query, arrayOf(usuario.nombreUsuario, usuario.clave))

        if (cursor.moveToFirst()) {
            inicio = true
            // Parseamos la fecha (si la guardaste como "dd-MM-yyyy")
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val fechaNacimiento = sdf.parse(cursor.getString(5))
            val usuario = DTOUsuario(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),fechaNacimiento,cursor.getString(6),cursor.getString(7))
            ZonaCompartida.setUsuarioRegistrado(usuario)
        }
        cursor.close()


        return inicio
    }

}