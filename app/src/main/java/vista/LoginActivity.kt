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

    private val daoUsuario = DAOUsuario(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ZonaCompartida.addActivity(this)

        // Comprobamos si hay conexión con el servidor nada más iniciar la aplicación
        val hilo = SocketConnection("Conexion",this)
        hilo.start()

        btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        btnIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        etNombre = findViewById<EditText>(R.id.editTextNombre)
        etPassword = findViewById<EditText>(R.id.editTextTextPassword)

        /**
         * Método que se ejecuta cuando el usuario le da click al botón de registrarse
         */
        btnRegistrarse.setOnClickListener(){
            if(ZonaCompartida.isIsOnline()){
                // Lanzamos la activity para registrar un nuevo usuario
                val intent = Intent(this, SignActivity::class.java)
                this.startActivity(intent)
            }else{
                Toast.makeText(this,R.string.e_Conexion,Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Método que se ejecuta cuando el usuario le da click al botón de iniciar sesión
         */
        btnIniciarSesion.setOnClickListener(){
            // Cogemos los datos introducidos por el usuario
            val usuario = DTOUsuario(etNombre.text.toString(),etPassword.text.toString())
            // Comprobamos si tenemos conexión con el servidor o no, en caso de haber conexion comprobamos
            // con los usuarios de la bbdd del servidor, sino comprobamos con los de la bbdd local
            if(ZonaCompartida.isIsOnline()){
                inicioSesionOnline(usuario)
            }else{
                inicioSesionOffline(usuario)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    fun inicioSesionOnline(usuario : DTOUsuario){
        // Nos conectamos al servidor para comprobar si existe un usuario con esa clave
        val hilo = SocketConnection("IniciarSesion",usuario)
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

    fun inicioSesionOffline(usuario: DTOUsuario){
        // Comprobamos si existe el usuario en la base de datos en local
        if(daoUsuario.signup(usuario)){
            Toast.makeText(this,R.string.a_InicioSesion,Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }else{
            Toast.makeText(this,R.string.e_NoRegistrado,Toast.LENGTH_SHORT).show()
        }
    }
}