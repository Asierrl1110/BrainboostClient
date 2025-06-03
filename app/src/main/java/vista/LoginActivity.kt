package vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.DAOUsuario
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOUsuario

/**
 * Activity en la que nos logeamos en la aplicación
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var btnRegistrarse : Button

    private lateinit var btnIniciarSesion : Button

    private lateinit var etNombre : EditText

    private lateinit var etPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ZonaCompartida.addActivity(this)

        btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        btnIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        etNombre = findViewById<EditText>(R.id.editTextNombre)
        etPassword = findViewById<EditText>(R.id.editTextTextPassword)

        /**
         * Método que se ejecuta cuando el usuario le da click al botón de registrarse
         */
        btnRegistrarse.setOnClickListener(){
                // Lanzamos la activity para registrar un nuevo usuario
                val intent = Intent(this, SignActivity::class.java)
                this.startActivity(intent)
        }

        /**
         * Método que se ejecuta cuando el usuario le da click al botón de iniciar sesión
         */
        btnIniciarSesion.setOnClickListener(){
            // Cogemos los datos introducidos por el usuario
            val usuario = DTOUsuario(etNombre.text.toString(),etPassword.text.toString())
            // Intentamos iniciar sesión con el usuario
            inicioSesion(usuario)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    /**
     * Método que permitir iniciar sesión en la aplicación
     */
    fun inicioSesion(usuario: DTOUsuario){
        // Nos conectamos al servidor para comprobar si existe un usuario con esa clave
        val hilo = SocketConnection("IniciarSesion",usuario,this)
        hilo.start()
        hilo.join()
        // Comprobamos si existe el usuario o no
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,R.string.a_InicioSesion,Toast.LENGTH_SHORT).show()
            // Añadimos el nuevo usuario registrado a la base de datos local
            val daoUsuario = DAOUsuario(this)
            daoUsuario.addUser(ZonaCompartida.getUsuarioRegistrado())
            // Lanzamos la activity principal
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }else{
            Toast.makeText(this,R.string.e_NoRegistrado, Toast.LENGTH_SHORT).show()
        }
    }
}