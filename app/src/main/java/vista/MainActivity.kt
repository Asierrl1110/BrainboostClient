package vista

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.example.clienteproyectofinal.AdaptadorMazo
import com.example.clienteproyectofinal.AdaptadorTarjeta
import com.example.clienteproyectofinal.DAOMazo
import com.example.clienteproyectofinal.DAOTarjeta
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import com.google.android.material.floatingactionbutton.FloatingActionButton
import modelo.DTOMazo
import modelo.DTOTarjeta
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var lvMazos : ListView

    private lateinit var adapter : AdaptadorMazo

    private lateinit var btnFlotante : FloatingActionButton

    private lateinit var tbMenu : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ZonaCompartida.addActivity(this)

        lvMazos = findViewById<ListView>(R.id.lvMazos)
        adapter = AdaptadorMazo(this,ZonaCompartida.getMazos())
        lvMazos.adapter = adapter


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

        lvMazos.setOnItemClickListener { parent, view, position, id ->
            val mazoSeleccionado = ZonaCompartida.getMazos()[position]
            val listaTarjetas = ZonaCompartida.getTarjetas().filter { it.idMazo == mazoSeleccionado.id }
            if(listaTarjetas.isNotEmpty()){
                ZonaCompartida.setTarjetasEstudio(listaTarjetas)
                val intent = Intent(this,EstudioActivity::class.java)
                this.startActivity(intent)
            }else{
                Toast.makeText(this,"Este mazo no tiene tarjetas",Toast.LENGTH_SHORT).show()
            }
        }



        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                result.data?.data?.let { uri ->
                    csvToMazo(uri)
                }
            }
        }
    }

    private fun csvToMazo(uri : Uri){
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use{ reader ->
                var valores : List<String>  = ArrayList<String>()
                var line = reader.readLine()
                if(line != null){
                    valores = line.toString().split(";")
                    val mazo = DTOMazo(valores.get(0),valores.get(1),ZonaCompartida.getUsuarioRegistrado().id)
                    val hilo = SocketConnection("AnadirMazo",mazo)
                    hilo.start()
                    hilo.join()
                    val nuevohilo = SocketConnection("IdMazo")
                    nuevohilo.start()
                    nuevohilo.join()
                    mazo.id = nuevohilo.idMazo
                    line = reader.readLine()
                    while(line != null){
                        valores = line.toString().split(";")
                        val tarjeta = DTOTarjeta(valores.get(0),valores.get(1),mazo.id)
                        val hiloTarjeta = SocketConnection("AnadirTarjeta",tarjeta)
                        hiloTarjeta.start()
                        hiloTarjeta.join()
                        line = reader.readLine()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuImportar ->{
                leerCSV()
            }
            R.id.menuSalir ->{
                finishAffinity()
            }
            R.id.menuPerfil ->{
                val intent = Intent(this,PerfilActivity::class.java)
                startActivity(intent)
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

    /**
     * Método que carga los mazos del usuario
     */
    private fun cargarMazos(){
        val hilo = SocketConnection("Mazos")
        hilo.setIdUsuario(ZonaCompartida.getUsuarioRegistrado().id)
        hilo.start()
        hilo.join()
        adapter = AdaptadorMazo(this,ZonaCompartida.getMazos())
        lvMazos.adapter = adapter
        val daoMazo = DAOMazo(this)
        daoMazo.deleteMazos(ZonaCompartida.getUsuarioRegistrado().id)
        daoMazo.addMazos(ZonaCompartida.getMazos(),ZonaCompartida.getUsuarioRegistrado().id)
    }

    /**
     * Método que carga las tarjetas del usuario
     */
    private fun cargarTarjetas(){
        val hilo = SocketConnection("TarjetasPorUsuario")
        hilo.setIdUsuario(ZonaCompartida.getUsuarioRegistrado().id)
        hilo.start()
        hilo.join()
        val daoTarjeta = DAOTarjeta(this)
        daoTarjeta.deleteTarjetas(ZonaCompartida.getUsuarioRegistrado().id)
        daoTarjeta.addTarjetas(ZonaCompartida.getTarjetas())
    }

    /**
     * Método que muestra el menu popup al mantener pulsado en un item de la list view
     */
    private fun showPopupMenu(view : View, position : Int){
        val menuPopup = PopupMenu(this,view)
        menuPopup.menuInflater.inflate(R.menu.menu_popup_mazo,menuPopup.menu)
        menuPopup.show()

        menuPopup.setOnMenuItemClickListener { menuItem ->
            val mazo = ZonaCompartida.getMazos()[position]
            when(menuItem.itemId){
                R.id.menuBorrarMazo ->{
                    mazo.idUsuario = ZonaCompartida.getUsuarioRegistrado().id
                    val hilo = SocketConnection("BorrarMazo",mazo)
                    hilo.start()
                    hilo.join()
                    if(hilo.isInstruccionRealizada){
                        Toast.makeText(this,"Mazo eliminado correctamente", Toast.LENGTH_SHORT).show()
                        ZonaCompartida.getMazos().removeAt(position)
                        adapter.notifyDataSetChanged()
                    }else{
                        Toast.makeText(this,"Error, no se pudo eliminar el mazo",Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.menuModificarMazo ->{
                    val intent = Intent(this, MazoActivity::class.java)
                    intent.putExtra("Caso","ModificarMazo")
                    intent.putExtra("Nombre",mazo.nombre)
                    intent.putExtra("Categoria",mazo.categoria)
                    intent.putExtra("IdMazo",mazo.id)
                    this.startActivity(intent)
                }
                R.id.menuAnadirTarjeta ->{
                    val intent = Intent(this,TarjetaActivity::class.java)
                    intent.putExtra("Caso","AnadirTarjeta")
                    intent.putExtra("IdMazo",mazo.id)
                    this.startActivity(intent)
                }
                R.id.menuExportarMazo ->{
                    val hilo = SocketConnection("TarjetasPorMazo")
                    hilo.setIdMazo(mazo.id)
                    hilo.start()
                    hilo.join()
                    val fichero = generarMazoCSV(mazo,ZonaCompartida.getTarjetas())
                    guardarCSV(this, mazo.nombre, fichero)
                }
            }
            true
        }
    }

    private fun leerCSV(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        resultLauncher.launch(intent)
    }

    /**
     * Método que permite guardar el fichero csv con el contenido de un mazo en
     * la carpeta de descargas
     */
    private fun guardarCSV(context: Context, nombreArchivo : String, contenido: String) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(contenido.toByteArray())
                Toast.makeText(this,"Mazo exportado correctamente",Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * Método que devuelve el contenido de un mazo y sus tarjetas en un texto en csv
     */
    private fun generarMazoCSV(mazo : DTOMazo, tarjetas : List<DTOTarjeta>) : String{
        val fichero = StringBuilder()
        fichero.append(mazo.nombre + ";" + mazo.categoria + "\n")

        for(tarjeta : DTOTarjeta in tarjetas){
            fichero.append(tarjeta.pregunta + ";" + tarjeta.respuesta + "\n")
        }

        return fichero.toString()
    }

    override fun onResume() {
        super.onResume()
        cargarMazos()
        cargarTarjetas()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }
}