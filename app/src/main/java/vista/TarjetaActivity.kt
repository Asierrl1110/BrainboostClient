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

        val caso = intent.getStringExtra("Caso")
        val pregunta = intent.getStringExtra("Pregunta")
        val respuesta = intent.getStringExtra("Respuesta")
        val idTarjeta = intent.getIntExtra("IdTarjeta",0)

        val idMazo = intent.getIntExtra("IdMazo",0)

        val etpregunta = findViewById<EditText>(R.id.etPregunta)
        val etrespuesta = findViewById<EditText>(R.id.etRespuesta)

        val btnAnadirTarjeta = findViewById<Button>(R.id.btnEnviarTarjeta)

        if(caso.equals("ModificarTarjeta")){
            etpregunta.setText(pregunta)
            etrespuesta.setText(respuesta)
        }

        btnAnadirTarjeta.setOnClickListener(){
            if(caso.equals("AnadirTarjeta")){
                val tarjeta = DTOTarjeta(etpregunta.text.toString(),etrespuesta.text.toString(),idMazo)
                val hilo = SocketConnection("AnadirTarjeta",tarjeta)
                hilo.start()
                hilo.join()
                if(hilo.isInstruccionRealizada){
                    Toast.makeText(this,"Tarjeta añadida al mazo " + idMazo,Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this,"Error al añadir la tarjeta",Toast.LENGTH_SHORT).show()
                }
            }else if(caso.equals("ModificarTarjeta")){
                val tarjetaAntigua = DTOTarjeta(idTarjeta,pregunta,respuesta,idMazo)
                val tarjetaNueva = DTOTarjeta(idTarjeta,etpregunta.text.toString(),etrespuesta.text.toString(),idMazo)
                val hilo = SocketConnection("ModificarTarjeta",tarjetaNueva,tarjetaAntigua)
                hilo.start()
                hilo.join()
                if(hilo.isInstruccionRealizada){
                    Toast.makeText(this,"Tarjeta modificada correctamente",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this,"Error al modificar la tarjeta",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}