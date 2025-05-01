package vista

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.example.clienteproyectofinal.AdaptadorMazo
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import com.google.android.material.floatingactionbutton.FloatingActionButton
import modelo.DTOMazo

class MainActivity : AppCompatActivity() {

    private lateinit var lvMazos : ListView

    private lateinit var adapter : AdaptadorMazo

    private lateinit var btnFlotante : FloatingActionButton

    private lateinit var tbMenu : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        cargarMazos()


        tbMenu = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tbMenu)

        btnFlotante = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnFlotante.setOnClickListener(){
            val intent = Intent(this, MazoActivity::class.java)
            intent.putExtra("Caso","AnadirMazo")
            this.startActivity(intent)
        }

        lvMazos.setOnItemLongClickListener { parent, view, position, id ->
            showPopupMenu(view,position)
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSalir ->{
                finishAffinity()
            }
            R.id.menuTarjetas ->{
                val intent = Intent(this, TarjetasActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    fun cargarMazos(){
        val hilo = SocketConnection("Mazos")
        hilo.setIdUsuario(ZonaCompartida.getUsuarioRegistrado().id)
        hilo.start()
        hilo.join()
        lvMazos = findViewById<ListView>(R.id.lvMazos)
        // val lista : MutableList<DTOMazo> = mutableListOf()
        // lista.add(DTOMazo(1,"Nombre","Categoria"))
        adapter = AdaptadorMazo(this,ZonaCompartida.getMazos())
        lvMazos.adapter = adapter
    }

    private fun showPopupMenu(view : View, position : Int){
        val menuPopup = PopupMenu(this,view)
        menuPopup.menuInflater.inflate(R.menu.menu_popup_mazo,menuPopup.menu)
        menuPopup.show()

        menuPopup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.btnborrarMazo ->{
                    val mazo = ZonaCompartida.getMazos()[position]
                    mazo.idUsuario = ZonaCompartida.getUsuarioRegistrado().id
                    val hilo = SocketConnection("BorrarMazo",mazo)
                    hilo.start()
                    hilo.join()
                    if(hilo.isInstruccionRealizada){
                        Toast.makeText(this,"Mazo eliminado correctamente", Toast.LENGTH_SHORT).show()
                        // actualizarMazos()
                    }else{
                        Toast.makeText(this,"Error, no se pudo eliminar el mazo",Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.btnmodificarMazo ->{
                    val intent = Intent(this, MazoActivity::class.java)
                    intent.putExtra("Caso","ModificarMazo")
                    intent.putExtra("Nombre",ZonaCompartida.getMazos()[position].nombre)
                    intent.putExtra("Categoria",ZonaCompartida.getMazos()[position].categoria)
                    intent.putExtra("IdMazo",ZonaCompartida.getMazos()[position].id)
                    this.startActivity(intent)
                }
                R.id.btnanadirtarjeta ->{
                    val intent = Intent(this,TarjetaActivity::class.java)
                    val mazo = ZonaCompartida.getMazos()[position]
                    intent.putExtra("IdMazo",mazo.id)
                    this.startActivity(intent)
                }
            }
            true
        }
    }
}