package vista

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clienteproyectofinal.AdaptadorMazo
import com.example.clienteproyectofinal.AdaptadorTarjeta
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida

class TarjetasActivity : AppCompatActivity() {

    lateinit var btnVolver : Button

    private lateinit var lvTarjetas : ListView

    private lateinit var adapter : AdaptadorTarjeta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarjetas)
        cargarTarjetas()

        btnVolver = findViewById<Button>(R.id.btnVolverTarjetas)

        btnVolver.setOnClickListener(){
            finish()
        }
    }

    fun cargarTarjetas(){
        val hilo = SocketConnection("TarjetasPorUsuario")
        hilo.setIdUsuario(ZonaCompartida.getUsuarioRegistrado().id)
        hilo.start()
        hilo.join()
        lvTarjetas = findViewById<ListView>(R.id.lvTarjetas)
        // val lista : MutableList<DTOMazo> = mutableListOf()
        // lista.add(DTOMazo(1,"Nombre","Categoria"))
        adapter = AdaptadorTarjeta(this, ZonaCompartida.getTarjetas())
        lvTarjetas.adapter = adapter
    }
}