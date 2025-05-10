package vista

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOUsuario

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign)
        ZonaCompartida.addActivity(this)

        val btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        val rbTerminos = findViewById<RadioButton>(R.id.radioButtonTerminos)
        val nombre = findViewById<EditText>(R.id.editTextNombre2)
        val password = findViewById<EditText>(R.id.editTextTextPassword2)

        /**
         * Método que se ejeucta cuando el usuario le da click al botón de registrarse
         */
        btnRegistrarse.setOnClickListener(){
            val usuario = DTOUsuario(nombre.text.toString(),password.text.toString())
            val hilo = SocketConnection("Registrarse",usuario)
            hilo.start()
            hilo.join()
            // Comprobamos si se ha podido registrar o no en el servidor
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Usuario registrado correctamente",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Error al intentar añadir el usuario",Toast.LENGTH_SHORT).show()
            }
        }

        rbTerminos.setOnCheckedChangeListener { buttonView, isChecked ->
            btnRegistrarse.isEnabled = isChecked
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }
}