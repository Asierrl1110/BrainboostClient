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
        val nombre = intent.getStringExtra("Nombre")
        val categoria = intent.getStringExtra("Categoria")
        val idMazo = intent.getIntExtra("IdMazo",0)

        val btnAnadirMazo = findViewById<Button>(R.id.btnanadirmazo)
        val nombreMazo = findViewById<EditText>(R.id.editTextNombreMazo)

        val spinner = findViewById<Spinner>(R.id.spinnerCategoria)
        val categorias = listOf<String>("Matematicas","Lengua","Historia","Inglés")
        val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,categorias)
        spinner.adapter = adaptador

        if(caso.equals("ModificarMazo")){
            nombreMazo.setText(nombre)
            spinner.setSelection(categorias.indexOf(categoria))
        }

        btnAnadirMazo.setOnClickListener(){
            if(caso.equals("AnadirMazo")){
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
            }else if (caso.equals("ModificarMazo")){
                val mazo = DTOMazo(idMazo,nombreMazo.text.toString(),spinner.selectedItem.toString(),ZonaCompartida.getUsuarioRegistrado().id)
                val mazoAntiguo = DTOMazo(idMazo,nombre,categoria,ZonaCompartida.getUsuarioRegistrado().id)
                val hilo = SocketConnection("ModificarMazo",mazo,mazoAntiguo)
                hilo.start()
                hilo.join()
                if(hilo.isInstruccionRealizada){
                    Toast.makeText(this,"Mazo modificado correctamente",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this,"Error, no se pudo modificados el mazo",Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}