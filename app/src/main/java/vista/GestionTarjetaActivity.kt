package vista

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOTarjeta

/**
 * Activity en la que añadimos una nueva tarjeta o modificamos los datos de una tarjeta ya existente
 */
class GestionTarjetaActivity : AppCompatActivity() {

    private lateinit var etPregunta : EditText

    private lateinit var etRespuesta : EditText

    private lateinit var btnAnadirTarjeta : Button

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

        etPregunta = findViewById<EditText>(R.id.etPregunta)
        etRespuesta = findViewById<EditText>(R.id.etRespuesta)
        btnAnadirTarjeta = findViewById<Button>(R.id.btnEnviarTarjeta)

        // En caso de que se haya llamado para modificar la tarjeta, se ponen los valores
        // de la tarjeta en los campos
        if(caso.equals("ModificarTarjeta")){
            etPregunta.setText(pregunta)
            etRespuesta.setText(respuesta)
        }

        /**
         * Método que se ejecuta cuando el usuario le da click al boton de añadir tarjeta
         */
        btnAnadirTarjeta.setOnClickListener(){
            if(etPregunta.text.toString().trim().equals("") || etRespuesta.text.toString().trim().equals("")){
                Toast.makeText(this,"Faltan campos por rellenar",Toast.LENGTH_SHORT).show()
            }else{
                when (caso){
                    "AnadirTarjeta" ->{
                        val tarjeta = DTOTarjeta(etPregunta.text.toString(),etRespuesta.text.toString(),idMazo)
                        anadirTarjeta(tarjeta)
                    }
                    "ModificarTarjeta" ->{
                        val tarjetaAntigua = DTOTarjeta(idTarjeta,pregunta,respuesta,idMazo)
                        val tarjetaNueva = DTOTarjeta(idTarjeta,etPregunta.text.toString(),etRespuesta.text.toString(),idMazo)
                        modificarTarjeta(tarjetaNueva,tarjetaAntigua)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    /**
     * Método que añade una nueva tarjeta en la bbdd del servidor
     */
    fun anadirTarjeta(tarjeta : DTOTarjeta){
        val hilo = SocketConnection("AnadirTarjeta",tarjeta,this)
        hilo.start()
        hilo.join()
        // Comprobamos si se ha podido añadir la tarjeta o no
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,R.string.a_TarjetaAnadida,Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this,R.string.e_TarjetaNoAnadida,Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que modifica los datos de una tarjeta existente de la bbdd del servidor
     */
    fun modificarTarjeta(tarjetaNueva : DTOTarjeta, tarjetaAntigua : DTOTarjeta){
        val hilo = SocketConnection("ModificarTarjeta",tarjetaNueva,tarjetaAntigua,this)
        hilo.start()
        hilo.join()
        // Comprobamos si se ha podida modificar la tarjeta o no
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,R.string.a_TarjetaModificada,Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this,R.string.e_TarjetaNoModificada,Toast.LENGTH_SHORT).show()
        }
    }
}