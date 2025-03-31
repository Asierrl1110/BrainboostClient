package com.example.clienteproyectofinal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign)

        val btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        val nombre = findViewById<EditText>(R.id.editTextNombre2)
        val password = findViewById<EditText>(R.id.editTextTextPassword2)

        btnRegistrarse.setOnClickListener(){
            val hilo = SocketEscritura("Registrarse")
            hilo.setNombre(nombre)
            hilo.setPassword(password)
            hilo.start()
            hilo.join()
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Usuario registrado correctamente",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Error al intentar añadir el usuario",Toast.LENGTH_SHORT).show()
            }
        }
    }
}