package vista

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOUsuario

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ZonaCompartida.addActivity(this)

        val tvPerfil = findViewById<TextView>(R.id.tvNombrePerfil)
        val btnCambiarClave = findViewById<Button>(R.id.btnCambiarClave)
        val btnBorrarUsuario = findViewById<Button>(R.id.btnBorrarUsuario)

        tvPerfil.setText(ZonaCompartida.getUsuarioRegistrado().nombre)

        btnCambiarClave.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.layout_cambiar_clave,null)
            builder.setTitle("Cambio de contraseña")
            builder.setView(view)
            // Botones
            builder.setPositiveButton("Aceptar") { dialog, _ ->
                val claveAntigua = view.findViewById<EditText>(R.id.etClaveAntigua)
                val claveNueva = view.findViewById<EditText>(R.id.etClaveNueva)
                if(claveAntigua.text.toString().equals(ZonaCompartida.getUsuarioRegistrado().clave)){
                    val usuario = DTOUsuario(ZonaCompartida.getUsuarioRegistrado().nombre,claveAntigua.text.toString(),claveNueva.text.toString())
                    val hilo = SocketConnection("CambiarClave",usuario)
                    hilo.start()
                    hilo.join()
                    if(hilo.isInstruccionRealizada){
                        Toast.makeText(this,"Contraseña cambiada correctamente",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Error al cambiar la contraseña",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"ERROR, Contraseña incorrecta",Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

        btnBorrarUsuario.setOnClickListener {
            val hilo = SocketConnection("BorrarUsuario",ZonaCompartida.getUsuarioRegistrado())
            hilo.start()
            hilo.join()
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Usuario eliminado correctamente",Toast.LENGTH_SHORT).show()
                ZonaCompartida.cerrarSesion()
            }else{
                Toast.makeText(this,"Error, no se pudo eliminar el perfil",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }
}