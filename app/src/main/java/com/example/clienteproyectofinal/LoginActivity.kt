package com.example.clienteproyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        btnRegistrarse.setOnClickListener(){
            val intent = Intent(this,SignActivity::class.java)
            this.startActivity(intent)
        }

    }
}