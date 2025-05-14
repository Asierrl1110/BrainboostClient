package vista

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.ZonaCompartida

class EstudioActivity : AppCompatActivity() {

    private var index : Int = 0

    private var acertadas : Int = 0

    private var falladas : Int = 0

    lateinit var tvPregunta : TextView

    lateinit var tvRespuesta : TextView

    lateinit var btnMostrarSolucion : Button

    lateinit var btnIncorrecta : Button

    lateinit var btnCorrecta : Button

    lateinit var btnAcabar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estudio)

        tvPregunta = findViewById<TextView>(R.id.tvPregunta)
        tvRespuesta = findViewById<TextView>(R.id.tvRespuesta)
        btnMostrarSolucion = findViewById<Button>(R.id.btnMostrarSolucion)
        btnAcabar = findViewById<Button>(R.id.btnAcabarEstudio)
        btnIncorrecta = findViewById<Button>(R.id.btnIncorrecta)
        btnCorrecta = findViewById<Button>(R.id.btnCorrecta)

        tvPregunta.text = ZonaCompartida.getTarjetasEstudio()[index].pregunta
        tvRespuesta.text = ZonaCompartida.getTarjetasEstudio()[index].respuesta

        btnMostrarSolucion.setOnClickListener {
            tvRespuesta.visibility = View.VISIBLE
            btnMostrarSolucion.visibility = View.GONE
            btnIncorrecta.visibility = View.VISIBLE
            btnCorrecta.visibility = View.VISIBLE
        }

        btnIncorrecta.setOnClickListener {
            tvRespuesta.visibility = View.INVISIBLE
            btnMostrarSolucion.visibility = View.VISIBLE
            btnIncorrecta.visibility = View.GONE
            btnCorrecta.visibility = View.GONE
            falladas++
            pasarTarjeta()
        }

        btnCorrecta.setOnClickListener {
            tvRespuesta.visibility = View.INVISIBLE
            btnMostrarSolucion.visibility = View.VISIBLE
            btnIncorrecta.visibility = View.GONE
            btnCorrecta.visibility = View.GONE
            acertadas++
            pasarTarjeta()
        }

        btnAcabar.setOnClickListener {
            finish()
        }
    }

    private fun pasarTarjeta(){
        if(index < ZonaCompartida.getTarjetasEstudio().size-1){
            index++
            tvPregunta.text = ZonaCompartida.getTarjetasEstudio()[index].pregunta
            tvRespuesta.text = ZonaCompartida.getTarjetasEstudio()[index].respuesta
        }else{
            btnAcabar.visibility = View.VISIBLE
            tvPregunta.text = "Acertadas: " + acertadas
            tvRespuesta.text = "Falladas: " + falladas
            btnMostrarSolucion.visibility = View.GONE
            btnIncorrecta.visibility = View.GONE
            btnCorrecta.visibility = View.GONE
            tvRespuesta.visibility = View.VISIBLE
        }
    }
}