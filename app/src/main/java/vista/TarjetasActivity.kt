package vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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

        lvTarjetas.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this,TarjetaActivity::class.java)
            val tarjeta = ZonaCompartida.getTarjetas()[position]
            intent.putExtra("Caso","ModificarTarjeta")
            intent.putExtra("IdTarjeta",tarjeta.id)
            intent.putExtra("Pregunta",tarjeta.pregunta)
            intent.putExtra("Respuesta",tarjeta.respuesta)
            intent.putExtra("IdMazo",tarjeta.idMazo)
            startActivity(intent)
        }

        lvTarjetas.setOnItemLongClickListener { parent, view, position, id ->
            showPopupMenu(view,position)
            true
        }
    }

    private fun showPopupMenu(view : View, position : Int){
        val menuPopup = PopupMenu(this,view)
        menuPopup.menuInflater.inflate(R.menu.menu_popup_tarjeta,menuPopup.menu)
        menuPopup.show()

        menuPopup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menuBorrarTarjeta ->{
                    val tarjeta = ZonaCompartida.getTarjetas()[position]
                    val hilo = SocketConnection("BorrarTarjeta",tarjeta)
                    hilo.start()
                    hilo.join()
                    if(hilo.isInstruccionRealizada){
                        Toast.makeText(this,"Tarjeta eliminada correctamente",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Error, no se pudo eliminar la tarjeta",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
    }

    fun cargarTarjetas(){
        val hilo = SocketConnection("TarjetasPorUsuario")
        hilo.setIdUsuario(ZonaCompartida.getUsuarioRegistrado().id)
        hilo.start()
        hilo.join()
        lvTarjetas = findViewById<ListView>(R.id.lvTarjetas)
        adapter = AdaptadorTarjeta(this, ZonaCompartida.getTarjetas())
        lvTarjetas.adapter = adapter
    }
}