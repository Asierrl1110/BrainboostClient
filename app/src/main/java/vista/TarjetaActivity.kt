package vista

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import modelo.DTOTarjeta

class TarjetaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarjeta)

        val idMazo = intent.getIntExtra("IdMazo",0)

        val pregunta = findViewById<EditText>(R.id.etPregunta)
        val respuesta = findViewById<EditText>(R.id.etRespuesta)

        val btnAnadirTarjeta = findViewById<Button>(R.id.btnEnviarTarjeta)

        btnAnadirTarjeta.setOnClickListener(){
            val tarjeta = DTOTarjeta(pregunta.text.toString(),respuesta.text.toString(),idMazo)
            val hilo = SocketConnection("AnadirTarjeta",tarjeta)
            hilo.start()
            hilo.join()
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,"Tarjeta añadida al mazo " + idMazo,Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Error al añadir el mazo",Toast.LENGTH_SHORT).show()
            }
        }
    }
}