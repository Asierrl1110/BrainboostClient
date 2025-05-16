package vista

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.DAOUsuario
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOUsuario
import java.text.SimpleDateFormat

class PerfilActivity : AppCompatActivity() {

    // Elementos de la activity
    private lateinit var tvNombreUsuario : TextView

    private lateinit var tvNombre : TextView

    private lateinit var tvApellidos : TextView

    private lateinit var tvFechaNacimiento : TextView

    private lateinit var tvGenero : TextView

    private lateinit var tvRol : TextView

    private lateinit var btnCambiarClave : Button

    private lateinit var btnBorrarUsuario : Button

    // Elementos del alert dialog
    private lateinit var etClaveAntigua : EditText

    private lateinit var etClaveNueva : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ZonaCompartida.addActivity(this)
        val formato = SimpleDateFormat("yyyy-MM-dd")

        tvNombreUsuario = findViewById<TextView>(R.id.tvNombreUsuario)
        tvNombre = findViewById<TextView>(R.id.tvNombre)
        tvApellidos = findViewById<TextView>(R.id.tvApellidos)
        tvFechaNacimiento = findViewById<TextView>(R.id.tvFechaNacimiento)
        tvGenero = findViewById<TextView>(R.id.tvGenero)
        tvRol = findViewById<TextView>(R.id.tvRol)
        btnCambiarClave = findViewById<Button>(R.id.btnCambiarClave)
        btnBorrarUsuario = findViewById<Button>(R.id.btnBorrarUsuario)

        // Mostramos los datos del usuario en los text view
        tvNombreUsuario.setText("Usuario: " + ZonaCompartida.getUsuarioRegistrado().nombreUsuario)
        tvNombre.setText("Nombre: " + ZonaCompartida.getUsuarioRegistrado().nombre)
        tvApellidos.setText("Apellidos: " + ZonaCompartida.getUsuarioRegistrado().apellidos)
        tvFechaNacimiento.setText("Fecha Nacimiento: " + formato.format(ZonaCompartida.getUsuarioRegistrado().fechaNacimiento))
        tvGenero.setText("Género: " + ZonaCompartida.getUsuarioRegistrado().genero)
        tvRol.setText("Rol: " + ZonaCompartida.getUsuarioRegistrado().rol)

        /**
         * Método que se ejecuta cuando el usuario pulsa sobre el botón de cambiar clave
         */
        btnCambiarClave.setOnClickListener {
            if(ZonaCompartida.isIsOnline()){
                mostrarAlertDialog()
            }else{
                Toast.makeText(this,R.string.e_Conexion,Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Método que se ejecuta cuando el usuario pulsa sobre el botón de borrar un usuario
         */
        btnBorrarUsuario.setOnClickListener {
            if(ZonaCompartida.isIsOnline()){
                val hilo = SocketConnection("BorrarUsuario",ZonaCompartida.getUsuarioRegistrado())
                hilo.start()
                hilo.join()
                if(hilo.isInstruccionRealizada){
                    Toast.makeText(this,R.string.a_UsuarioEliminado,Toast.LENGTH_SHORT).show()
                    val daoUsuario = DAOUsuario(this)
                    daoUsuario.deleteUser(ZonaCompartida.getUsuarioRegistrado())
                    ZonaCompartida.cerrarSesion()
                }else{
                    Toast.makeText(this,R.string.e_UsuarioNoEliminado,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,R.string.e_Conexion,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    /**
     * Método que muestra el alert dialog de cambio de contraseña
     */
    fun mostrarAlertDialog(){
        // Montamos el alert dialog para poder introducir los datos de contraseña
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.layout_cambiar_clave,null)
        builder.setTitle("Cambio de contraseña")
        builder.setView(view)
        // Botones de aceptar y cancelar y sus metodos
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            etClaveAntigua = view.findViewById<EditText>(R.id.etClaveAntigua)
            etClaveNueva = view.findViewById<EditText>(R.id.etClaveNueva)
            if(etClaveAntigua.text.toString().equals(ZonaCompartida.getUsuarioRegistrado().clave)){
                modificarClave()
            }else{
                Toast.makeText(this,R.string.e_ClaveIncorrecta,Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    /**
     * Método que modifica la clave del usuario que esta logeado
     */
    fun modificarClave(){
        val usuario = DTOUsuario(ZonaCompartida.getUsuarioRegistrado().nombreUsuario,etClaveAntigua.text.toString(),etClaveNueva.text.toString())
        // Modificamos los datos del usuario en la bbdd del servidor
        val hilo = SocketConnection("CambiarClave",usuario)
        hilo.start()
        hilo.join()
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,R.string.a_ClaveCambiada,Toast.LENGTH_SHORT).show()
            // Si se ha modificado correctamente en la bbdd del servidor, modificamos los datos
            // en la bbdd en local y en los datos cargados en memoria
            ZonaCompartida.getUsuarioRegistrado().clave = etClaveNueva.text.toString()
            val daoUsuario = DAOUsuario(this)
            daoUsuario.changePassword(etClaveNueva.text.toString(),DTOUsuario(ZonaCompartida.getUsuarioRegistrado().id,ZonaCompartida.getUsuarioRegistrado().nombreUsuario,etClaveAntigua.text.toString()))
        }else{
            Toast.makeText(this,R.string.e_ClaveNoCambiada,Toast.LENGTH_SHORT).show()
        }
    }
}