package vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida

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
            val intent = Intent(this, SignActivity::class.java)
            this.startActivity(intent)
        }

        btnIniciarSesion.setOnClickListener(){
            val hilo = SocketConnection("IniciarSesion")
            hilo.setNombreUsuario(nombre.text.toString())
            hilo.setClaveUsuario(password.text.toString())
            hilo.start()
            hilo.join()
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Inicio de sesion correctamente",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            }else{
                Toast.makeText(this,"Error, nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

    }
}