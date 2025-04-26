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

class MazoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mazo)

        val caso = intent.getStringExtra("Caso")

        val btnAnadirMazo = findViewById<Button>(R.id.btnanadirmazo)
        val nombreMazo = findViewById<EditText>(R.id.editTextNombreMazo)

        val spinner = findViewById<Spinner>(R.id.spinnerCategoria)
        val categorias = listOf<String>("Matematicas","Lengua","Historia","Inglés")
        val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,categorias)
        spinner.adapter = adaptador

        btnAnadirMazo.setOnClickListener(){
            if(caso.equals("Anadir")){
                val mazo = DTOMazo(nombreMazo.text.toString(),spinner.selectedItem.toString(),ZonaCompartida.getUsuarioRegistrado().id)
                val hilo = SocketConnection("AnadirMazo",mazo)
                hilo.start()
                hilo.join()
                if(hilo.isInstruccionRealizada){
                    Toast.makeText(this,"Mazo creado correctamente",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this,"Error, no se pudo añadir el mazo",Toast.LENGTH_SHORT).show()
                }
            }else if(caso.equals("Modificar")){

            }
        }


    }
}