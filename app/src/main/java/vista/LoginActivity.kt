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
import modelo.DTOUsuario

/**
 * Activity en la que nos logeamos en la aplicación
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ZonaCompartida.addActivity(this)

        val btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        val nombre = findViewById<EditText>(R.id.editTextNombre)
        val password = findViewById<EditText>(R.id.editTextTextPassword)

        /**
         * Método que se ejecuta cuando el usuario le da click al botón de registrarse
         */
        btnRegistrarse.setOnClickListener(){
            val intent = Intent(this, SignActivity::class.java)
            this.startActivity(intent)
        }

        /**
         * Método que se ejecuta cuando el usuario le da click al botón de iniciar sesión
         */
        btnIniciarSesion.setOnClickListener(){
            val usuario = DTOUsuario(nombre.text.toString(),password.text.toString())
            val hilo = SocketConnection("IniciarSesion",usuario)
            hilo.start()
            hilo.join()
            // Comprobamos si se ha podido iniciar sesión o no
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Inicio de sesion correctamente",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            }else{
                Toast.makeText(this,"Error, nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }
}