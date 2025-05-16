package vista

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.ZonaCompartida

class EstudioActivity : AppCompatActivity() {

    private var index : Int = 0

    private var acertadas : Int = 0

    private var falladas : Int = 0

    private lateinit var etPregunta : EditText

    private lateinit var etRespuesta : EditText

    private lateinit var btnIniciarEstudio : Button

    private lateinit var btnMostrarSolucion : Button

    private lateinit var btnIncorrecta : Button

    private lateinit var btnCorrecta : Button

    private lateinit var btnAcabar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estudio)
        ZonaCompartida.addActivity(this)

        etPregunta = findViewById<EditText>(R.id.etPregunta)
        etRespuesta = findViewById<EditText>(R.id.etRespuesta)
        btnIniciarEstudio = findViewById<Button>(R.id.btnIniciarEstudio)
        btnMostrarSolucion = findViewById<Button>(R.id.btnMostrarSolucion)
        btnAcabar = findViewById<Button>(R.id.btnAcabarEstudio)
        btnIncorrecta = findViewById<Button>(R.id.btnIncorrecta)
        btnCorrecta = findViewById<Button>(R.id.btnCorrecta)

        // Mostramos los datos del mazo que vamos a estudiar
        etPregunta.setText(intent.getStringExtra("Nombre") + " - " + intent.getStringExtra("Categoria"))
        etRespuesta.setText(intent.getStringExtra("Descripcion"))

        btnIniciarEstudio.setOnClickListener {
            etRespuesta.visibility = View.INVISIBLE
            etPregunta.setText(ZonaCompartida.getTarjetasEstudio()[index].pregunta)
            etRespuesta.setText(ZonaCompartida.getTarjetasEstudio()[index].respuesta)
            btnIniciarEstudio.visibility = View.GONE
            btnMostrarSolucion.visibility = View.VISIBLE
        }

        /**
         * Método que se ejecuta cuando el usuario pulsa en el botón de mostrar solución
         */
        btnMostrarSolucion.setOnClickListener {
            etRespuesta.visibility = View.VISIBLE
            btnMostrarSolucion.visibility = View.GONE
            btnIncorrecta.visibility = View.VISIBLE
            btnCorrecta.visibility = View.VISIBLE
        }

        /**
         * Método que se ejecuta cuando el usuario pulsa en el botón de respuesta incorrecta
         */
        btnIncorrecta.setOnClickListener {
            etRespuesta.visibility = View.INVISIBLE
            btnMostrarSolucion.visibility = View.VISIBLE
            btnIncorrecta.visibility = View.GONE
            btnCorrecta.visibility = View.GONE
            falladas++
            pasarTarjeta()
        }

        /**
         * Método que se ejecuta cuando el usuario pulsa en el botón de respuesta correcta
         */
        btnCorrecta.setOnClickListener {
            etRespuesta.visibility = View.INVISIBLE
            btnMostrarSolucion.visibility = View.VISIBLE
            btnIncorrecta.visibility = View.GONE
            btnCorrecta.visibility = View.GONE
            acertadas++
            pasarTarjeta()
        }

        /**
         * Método que se ejecuta cuando el usuario pulsa en el botón de acabar de estudiar
         */
        btnAcabar.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    /**
     * Método que pasa a la siguiente tarjeta del mazo que se esta estudiando
     */
    private fun pasarTarjeta(){
        // Comprobamos si estamos en la última tarjeta o no
        if(index < ZonaCompartida.getTarjetasEstudio().size-1){
            // Pasamos a la siguiente tarjeta y mostramos la pregunta
            index++
            etPregunta.setText(ZonaCompartida.getTarjetasEstudio()[index].pregunta)
            etRespuesta.setText(ZonaCompartida.getTarjetasEstudio()[index].respuesta)
        }else{
            // Ocultamos los elementos y dejamos solo visible los text view para mostrar el
            // número de preguntas acertadas y falladas y un boton para salir de la pantalla
            btnAcabar.visibility = View.VISIBLE
            etPregunta.setText("Acertadas: " + acertadas)
            etRespuesta.setText("Falladas: " + falladas)
            btnMostrarSolucion.visibility = View.GONE
            btnIncorrecta.visibility = View.GONE
            btnCorrecta.visibility = View.GONE
            etRespuesta.visibility = View.VISIBLE
        }
    }

}