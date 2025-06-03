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
import com.example.clienteproyectofinal.AdaptadorTarjeta
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida

class TarjetasActivity : AppCompatActivity() {

    private lateinit var lvTarjetas : ListView

    private lateinit var btnVolver : Button

    private lateinit var adapter : AdaptadorTarjeta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarjetas)
        ZonaCompartida.addActivity(this)

        btnVolver = findViewById<Button>(R.id.btnVolverTarjetas)
        lvTarjetas = findViewById<ListView>(R.id.lvTarjetas)
        adapter = AdaptadorTarjeta(this, ZonaCompartida.getTarjetas())
        lvTarjetas.adapter = adapter

        /**
         * Método que se ejecuta cuando el usuario pulsa en el boton de volver
         */
        btnVolver.setOnClickListener(){
            finish()
        }

        /**
         * Método que se ejecuta cuando el usuario le da click a un elemento de la lista de tarjetas
         */
        lvTarjetas.setOnItemClickListener { parent, view, position, id ->
            // Si hay conexion con el servidor, lanzamos la activity de gestion de tarjetas
            // con el objetivo de modificar los datos de una tarjeta
                val intent = Intent(this,GestionTarjetaActivity::class.java)
                val tarjeta = ZonaCompartida.getTarjetas()[position]
                intent.putExtra("Caso","ModificarTarjeta")
                intent.putExtra("IdTarjeta",tarjeta.id)
                intent.putExtra("Pregunta",tarjeta.pregunta)
                intent.putExtra("Respuesta",tarjeta.respuesta)
                intent.putExtra("IdMazo",tarjeta.idMazo)
                startActivity(intent)
        }

        /**
         * Método que se ejecuta cuando el usuario mantiene pulsado sobre un elemento de la lista de tarjetas
         */
        lvTarjetas.setOnItemLongClickListener { parent, view, position, id ->
            showPopupMenu(view,position)
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    /**
     * Método que visualiza el menu popup al mantener pulsado sobre un elemento de la lista de tarjetas
     */
    private fun showPopupMenu(view : View, position : Int){
        // Mostramos el popup
        val menuPopup = PopupMenu(this,view)
        menuPopup.menuInflater.inflate(R.menu.menu_popup_tarjeta,menuPopup.menu)
        menuPopup.show()

        /**
         * Método que se ejecuta cuando pulsamos sobre una opcion del menu popup
         * En funcion de la opcion que sea, se realizaran unas instrucciones u otras
         */
        menuPopup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menuBorrarTarjeta ->{
                        borrarTarjeta(position)
                }
            }
            true
        }
    }

    /**
     * Método que elimina una tarjeta de la bbdd del servidor
     */
    fun borrarTarjeta(position: Int){
        val tarjeta = ZonaCompartida.getTarjetas()[position]
        val hilo = SocketConnection("BorrarTarjeta",tarjeta, this)
        hilo.start()
        hilo.join()
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,"Tarjeta eliminada correctamente",Toast.LENGTH_SHORT).show()
            ZonaCompartida.getTarjetas().removeAt(position)
            adapter.notifyDataSetChanged()
        }else{
            Toast.makeText(this,"Error, no se pudo eliminar la tarjeta",Toast.LENGTH_SHORT).show()
        }
    }
}