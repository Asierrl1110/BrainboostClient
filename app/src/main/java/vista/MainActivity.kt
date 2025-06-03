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

    private lateinit var btnFlotante : FloatingActionButton

    private lateinit var tbMenu : Toolbar

    private lateinit var adapter : AdaptadorMazo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ZonaCompartida.addActivity(this)

        btnFlotante = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        lvMazos = findViewById<ListView>(R.id.lvMazos)
        tbMenu = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tbMenu)

        cargarMazos()
        cargarTarjetas()

        /**
         * Método que se ejecuta cuando el usuario pulsa en el botón flotante
         */
        btnFlotante.setOnClickListener{
                val intent = Intent(this, GestionMazoActivity::class.java)
                intent.putExtra("Caso","AnadirMazo")
                this.startActivity(intent)
        }

        /**
         * Método que se ejecuta cuando el usuario mantiene pulsado sobre un elemento de la lista de mazos
         */
        lvMazos.setOnItemLongClickListener { parent, view, position, id ->
            showPopupMenu(view,position)
            true
        }

        /**
         * Método que se ejecuta cuando el usuario pulsa sobre un elemento de la lista de mazos
         */
        lvMazos.setOnItemClickListener { parent, view, position, id ->
            val mazoSeleccionado = ZonaCompartida.getMazos()[position]
            // Filtramos por las tarjetas del mazo que ha sido pulsado
            val listaTarjetas = ZonaCompartida.getTarjetas().filter { it.idMazo == mazoSeleccionado.id }
            // En caso de que el mazo tenga tarjetas, entonces lanzamos la activity de estudio
            if(listaTarjetas.isNotEmpty()){
                ZonaCompartida.setTarjetasEstudio(listaTarjetas)
                val intent = Intent(this,EstudioActivity::class.java)
                intent.putExtra("Nombre",mazoSeleccionado.nombre)
                intent.putExtra("Categoria",mazoSeleccionado.categoria)
                intent.putExtra("Descripcion",mazoSeleccionado.descripcion)
                this.startActivity(intent)
            }else{
                Toast.makeText(this,R.string.e_NoTarjetas,Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Método que se lanza despues de haber seleccionado un archivo
         */
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                result.data?.data?.let { uri ->
                    csvToMazo(uri)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }

    /**
     * Método que carga el layout en la toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    /**
     * Método que se ejecuta cuando el usuario pulsa sobre una opcion del menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuImportar ->{
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/comma-separated-values"
                    }
                    resultLauncher.launch(intent)
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
            R.id.menuSincronismo ->{
                    cargarMazos()
                    cargarTarjetas()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this,R.string.a_Sincronizado,Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Método que carga los mazos del usuario
     */
    private fun cargarMazos(){
                val hilo = SocketConnection("Mazos",ZonaCompartida.getUsuarioRegistrado().id,this)
                hilo.start()
                hilo.join()
        val daoMazo = DAOMazo(this)
        daoMazo.deleteMazos(ZonaCompartida.getUsuarioRegistrado().id)
        daoMazo.addMazos(ZonaCompartida.getMazos(),ZonaCompartida.getUsuarioRegistrado().id)
        adapter = AdaptadorMazo(this,ZonaCompartida.getMazos())
        lvMazos.adapter = adapter
    }

    /**
     * Método que carga las tarjetas del usuario
     */
    private fun cargarTarjetas(){
            val hilo = SocketConnection("TarjetasPorUsuario",ZonaCompartida.getUsuarioRegistrado().id,this)
            hilo.start()
            hilo.join()
        val daoTarjeta = DAOTarjeta(this)
        daoTarjeta.deleteTarjetas(ZonaCompartida.getUsuarioRegistrado().id)
            daoTarjeta.addTarjetas(ZonaCompartida.getTarjetas())
    }

    /**
     * Método que devuelve el contenido de un mazo y sus tarjetas en un texto en csv
     */
    private fun generarMazoCSV(mazo : DTOMazo, tarjetas : List<DTOTarjeta>) : String{
        val fichero = StringBuilder()
        fichero.append(mazo.nombre + ";" + mazo.categoria + ";" + mazo.descripcion + "\n")

        for(tarjeta : DTOTarjeta in tarjetas){
            fichero.append(tarjeta.pregunta + ";" + tarjeta.respuesta + "\n")
        }

        return fichero.toString()
    }

    /**
     * Método que permite guardar el fichero csv con el contenido de un mazo en
     * la carpeta de descargas
     */
    private fun exportarCSV(context: Context, nombreArchivo : String, contenido: String) {
        // Obtenemos el Content Resolver
        val contentResolver = context.contentResolver
        // Creamos los metadatos del archivo que vamos a guardar
        val contentValues = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "text/comma-separated-values")
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        // Insertamos un nuevo archivo con esos metadatos en el almacenamiento externo
        // y obtenemos su uri
        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        // Escribimos el contenido del fichero
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(contenido.toByteArray())
                Toast.makeText(context,R.string.a_Exportado,Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * Metodo que importa un nuevo mazo a partir del contenido
     * de un archivo de la uri
     */
    private fun csvToMazo(uri : Uri){
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use{ reader ->
                var valores : List<String>  = ArrayList<String>()
                // Leemos la primera linea y añadimos un mazo con los datos
                var line = reader.readLine()
                if(line != null){
                    // Importamos el mazo con los datos
                    valores = line.toString().split(";")
                    val mazo = DTOMazo(valores.get(0),valores.get(1),valores.get(2),ZonaCompartida.getUsuarioRegistrado().id)
                    val hilo = SocketConnection("AnadirMazo",mazo, this)
                    hilo.start()
                    hilo.join()
                    if(hilo.isInstruccionRealizada){
                        // Leemos las siguientes lineas y añadimos tarjetas con los datos de esas lineas
                        line = reader.readLine()
                        if(line != null){
                            // Obtenemos el id del nuevo mazo creado para añadir tarjetas a ese mazo
                            val nuevohilo = SocketConnection("IdMazo",this)
                            nuevohilo.start()
                            nuevohilo.join()
                            mazo.id = nuevohilo.idMazo
                            while(line != null){
                                valores = line.toString().split(";")
                                val tarjeta = DTOTarjeta(valores.get(0),valores.get(1),mazo.id)
                                val hiloTarjeta = SocketConnection("AnadirTarjeta",tarjeta, this)
                                hiloTarjeta.start()
                                hiloTarjeta.join()
                                line = reader.readLine()
                            }
                        }
                    }else{
                        Toast.makeText(this,R.string.e_NoAnadirMazo,Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        cargarMazos()
        cargarTarjetas()
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
                    borrarMazo(mazo, position)
                }
                R.id.menuModificarMazo ->{
                    modificarMazo(mazo)
                }
                R.id.menuAnadirTarjeta ->{
                    anadirTarjeta(mazo.id)
                }
                R.id.menuExportarMazo ->{
                    val listaTarjetas = ZonaCompartida.getTarjetas().filter { it.idMazo == mazo.id }
                    ZonaCompartida.setTarjetasEstudio(listaTarjetas)
                    val fichero = generarMazoCSV(mazo,ZonaCompartida.getTarjetasEstudio())
                    exportarCSV(this, mazo.nombre, fichero)
                }
            }
            true
        }
    }

    /**
     * Método que borra un mazo de la bbdd del servidor
     */
    private fun borrarMazo(mazo : DTOMazo, position: Int){
            mazo.idUsuario = ZonaCompartida.getUsuarioRegistrado().id
            val hilo = SocketConnection("BorrarMazo",mazo,this)
            hilo.start()
            hilo.join()
            if(hilo.isInstruccionRealizada){
                Toast.makeText(this,R.string.a_MazoEliminado, Toast.LENGTH_SHORT).show()
                ZonaCompartida.getMazos().removeAt(position)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(this,R.string.e_MazoNoEliminado,Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Método que modifica un mazo de la bbdd del servidor
     */
    private fun modificarMazo(mazo: DTOMazo) {
            val intent = Intent(this, GestionMazoActivity::class.java)
            intent.putExtra("Caso","ModificarMazo")
            intent.putExtra("Nombre",mazo.nombre)
            intent.putExtra("Categoria",mazo.categoria)
            intent.putExtra("Descripcion",mazo.descripcion)
            intent.putExtra("IdMazo",mazo.id)
            this.startActivity(intent)
    }

    /**
     * Método que añade un mazo a la bbdd del servidor
     */
    private fun anadirTarjeta(idMazo : Int){
            val intent = Intent(this,GestionTarjetaActivity::class.java)
            intent.putExtra("Caso","AnadirTarjeta")
            intent.putExtra("IdMazo",idMazo)
            this.startActivity(intent)
    }
}