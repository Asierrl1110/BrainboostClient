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
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOTarjeta

/**
 * Activity en la que añadimos una nueva tarjeta o modificamos los datos de una tarjeta ya existente
 */
class TarjetaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarjeta)
        ZonaCompartida.addActivity(this)

        // Recibimos unos valores en la activity
        // Recibimos el caso para que se ha llamado, sea para modificar una tarjeta o crear una nueva
        val caso = intent.getStringExtra("Caso")
        val pregunta = intent.getStringExtra("Pregunta")
        val respuesta = intent.getStringExtra("Respuesta")
        val idTarjeta = intent.getIntExtra("IdTarjeta",0)
        val idMazo = intent.getIntExtra("IdMazo",0)

        val etpregunta = findViewById<EditText>(R.id.etPregunta)
        val etrespuesta = findViewById<EditText>(R.id.etRespuesta)
        val btnAnadirTarjeta = findViewById<Button>(R.id.btnEnviarTarjeta)

        // En caso de que se haya llamado para modificar la tarjeta, se ponen los valores
        // de la tarjeta en los campos
        if(caso.equals("ModificarTarjeta")){
            etpregunta.setText(pregunta)
            etrespuesta.setText(respuesta)
        }

        /**
         * Método que se ejecuta cuando el usuario le da click al boton de añadir tarjeta
         */
        btnAnadirTarjeta.setOnClickListener(){
            when (caso){
                "AnadirTarjeta" ->{
                    val tarjeta = DTOTarjeta(etpregunta.text.toString(),etrespuesta.text.toString(),idMazo)
                    val hilo = SocketConnection("AnadirTarjeta",tarjeta)
                    hilo.start()
                    hilo.join()
                    // Comprobamos si se ha podido añadir la tarjeta o no
                    if(hilo.isInstruccionRealizada){
                        Toast.makeText(this,"Tarjeta añadida al mazo " + idMazo,Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this,"Error al añadir la tarjeta",Toast.LENGTH_SHORT).show()
                    }
                }
                "ModificarTarjeta" ->{
                    // Variable que almacena los datos originales de la tarjeta
                    val tarjetaAntigua = DTOTarjeta(idTarjeta,pregunta,respuesta,idMazo)
                    // Variable que almacena los nuevos datos de la tarjeta
                    val tarjetaNueva = DTOTarjeta(idTarjeta,etpregunta.text.toString(),etrespuesta.text.toString(),idMazo)
                    val hilo = SocketConnection("ModificarTarjeta",tarjetaNueva,tarjetaAntigua)
                    hilo.start()
                    hilo.join()
                    // Comprobamos si se ha podida modificar la tarjeta o no
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

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }
}