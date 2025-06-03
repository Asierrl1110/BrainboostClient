package vista

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOMazo

class GestionMazoActivity : AppCompatActivity() {

    private lateinit var btnAnadirMazo : Button

    private lateinit var etNombreMazo : EditText

    private lateinit var spCategoria : Spinner

    private lateinit var etDescripcion : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mazo)
        ZonaCompartida.addActivity(this)

        // Recibimos unos valores en la activity
        // Recibimos el caso para que se ha llamado, sea para modificar un mazo o crear uno nuevo
        val caso = intent.getStringExtra("Caso")
        val nombre = intent.getStringExtra("Nombre")
        val categoria = intent.getStringExtra("Categoria")
        val descripcion = intent.getStringExtra("Descripcion")
        val idMazo = intent.getIntExtra("IdMazo",0)

        btnAnadirMazo = findViewById<Button>(R.id.btnanadirmazo)
        etNombreMazo = findViewById<EditText>(R.id.editTextNombreMazo)
        spCategoria = findViewById<Spinner>(R.id.spinnerCategoria)
        etDescripcion = findViewById<EditText>(R.id.etDescripcion)

        // Montamos el spinner con los posibles valores de la categoria
        val categorias = listOf<String>("Matematicas","Lengua","Historia","Inglés")
        val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,categorias)
        spCategoria.adapter = adaptador

        // En caso de que se haya llamado para modificar el mazo, se ponen los valores
        // del mazo en los campos
        if(caso.equals("ModificarMazo")){
            etNombreMazo.setText(nombre)
            spCategoria.setSelection(categorias.indexOf(categoria))
            etDescripcion.setText(descripcion)
        }

        /**
         * Método que se ejecuta cuando el usuario le da click al boton de añadir mazo
         */
        btnAnadirMazo.setOnClickListener(){
            if(etNombreMazo.text.toString().trim().equals("") || etDescripcion.text.toString().trim().equals("")){
               Toast.makeText(this,"Faltan campos",Toast.LENGTH_SHORT).show()
            }else{
                when (caso){
                    "AnadirMazo" ->{
                        val mazo = DTOMazo(etNombreMazo.text.toString(),spCategoria.selectedItem.toString(),etDescripcion.text.toString(),ZonaCompartida.getUsuarioRegistrado().id)
                        anadirMazo(mazo)
                    }
                    "ModificarMazo" ->{
                        val mazoNuevo = DTOMazo(idMazo,etNombreMazo.text.toString(),spCategoria.selectedItem.toString(),etDescripcion.text.toString(),ZonaCompartida.getUsuarioRegistrado().id)
                        val mazoAntiguo = DTOMazo(idMazo,nombre,categoria,descripcion,ZonaCompartida.getUsuarioRegistrado().id)
                        modificarMazo(mazoNuevo, mazoAntiguo)
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
     * Método que añade un mazo a la bbdd del servidor
     */
    fun anadirMazo(mazo : DTOMazo){
        val hilo = SocketConnection("AnadirMazo",mazo,this)
        hilo.start()
        hilo.join()
        // Comprobamos si se ha añadido el mazo o no
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,R.string.a_MazoCreado,Toast.LENGTH_SHORT).show()
            // ZonaCompartida.getMazos().add(mazo)
            finish()
        }else{
            Toast.makeText(this,R.string.e_NoAnadirMazo,Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que modifica los datos de un mazo existente de la bbdd del servidor
     */
    fun modificarMazo(mazo : DTOMazo, mazoAntiguo : DTOMazo){
        val hilo = SocketConnection("ModificarMazo",mazo,mazoAntiguo,this)
        hilo.start()
        hilo.join()
        // Comprobamos si se ha modificado el mazo no
        if(hilo.isInstruccionRealizada){
            Toast.makeText(this,R.string.a_MazoModificado,Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this,R.string.e_NoModificarMazo,Toast.LENGTH_SHORT).show()
        }
    }
}