package com.example.clienteproyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        val nombre = findViewById<EditText>(R.id.editTextNombre)
        val password = findViewById<EditText>(R.id.editTextTextPassword)

        btnRegistrarse.setOnClickListener(){
            val intent = Intent(this,SignActivity::class.java)
            this.startActivity(intent)
        }

        btnIniciarSesion.setOnClickListener(){
            val hilo = SocketEscritura("IniciarSesion")
            hilo.setNombre(nombre)
            hilo.setPassword(password)
            hilo.start()
            hilo.join()
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Inicio de sesion correctamente",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                this.startActivity(intent)
            }else{
                Toast.makeText(this,"Error, nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

    }
}