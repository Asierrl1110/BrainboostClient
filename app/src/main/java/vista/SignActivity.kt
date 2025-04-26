package vista

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import modelo.DTOUsuario

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign)

        val btnRegistrarse = findViewById<Button>(R.id.buttonRegistrarse)
        val nombre = findViewById<EditText>(R.id.editTextNombre2)
        val password = findViewById<EditText>(R.id.editTextTextPassword2)

        btnRegistrarse.setOnClickListener(){
            val usuario = DTOUsuario(nombre.text.toString(),password.text.toString());
            val hilo = SocketConnection("Registrarse",usuario)
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