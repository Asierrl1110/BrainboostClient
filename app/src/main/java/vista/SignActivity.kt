package vista

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clienteproyectofinal.R
import com.example.clienteproyectofinal.SocketConnection
import com.example.clienteproyectofinal.ZonaCompartida
import modelo.DTOUsuario
import java.util.Calendar

class SignActivity : AppCompatActivity() {

    private lateinit var btnRegistrarse : Button

    private lateinit var rbTerminos : RadioButton

    private lateinit var etNombreUsuario : EditText

    private lateinit var etPassword : EditText

    private lateinit var etNombre : EditText

    private lateinit var etApellidos : EditText

    private lateinit var etFechaNacimiento : EditText

    private lateinit var spGenero : Spinner

    private lateinit var spRol : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign)
        ZonaCompartida.addActivity(this)

        btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        rbTerminos = findViewById<RadioButton>(R.id.rbTerminos)
        etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        etPassword = findViewById<EditText>(R.id.etClave)
        etNombre = findViewById<EditText>(R.id.etNombre)
        etApellidos = findViewById<EditText>(R.id.etApellidos)
        etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        spGenero = findViewById<Spinner>(R.id.spGenero)
        spRol = findViewById<Spinner>(R.id.spRol)

        val generos = listOf("Hombre", "Mujer", "No binario")
        val roles = listOf(
            "Estudiante",
            "Opositor",
            "Profesor",
            "Investigador",
            "Tutor",
            "Profesional en formación",
            "Empleado en entrenamiento",
            "Entrevistado",
            "Autodidacta",
            "Usuario casual"
        )
        val adapterGenero = ArrayAdapter(this, android.R.layout.simple_spinner_item, generos)
        val adapterRol = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        spGenero.adapter = adapterGenero
        spRol.adapter = adapterRol

        /**
         * Método que se ejecuta cuando el usuario pulsa en el text de fecha de nacimiento
         */
        etFechaNacimiento.setOnClickListener {
            // Obtener la fecha actual
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Crear y mostrar el DatePickerDialog
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Mostrar la fecha seleccionada en el EditText (formato: DD/MM/YYYY)
                val fecha = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                etFechaNacimiento.setText(fecha)
            }, year, month, day)

            datePicker.show()
        }

        /**
         * Método que se ejeucta cuando el usuario le da click al botón de registrarse
         */
        btnRegistrarse.setOnClickListener(){
            if(etNombreUsuario.text.isNotEmpty() && etPassword.text.isNotEmpty() &&
                etNombre.text.isNotEmpty() && etApellidos.text.isNotEmpty() && etFechaNacimiento.text.isNotEmpty()){
                // Cogemos los datos que el usuario ha introducido en los cuadros de texto
                val usuario = DTOUsuario(etNombreUsuario.text.toString(),etPassword.text.toString(), etNombre.text.toString(), etApellidos.text.toString(), etFechaNacimiento.text.toString(), spGenero.selectedItem.toString(), spRol.selectedItem.toString())
                val hilo = SocketConnection("Registrarse",usuario, this)
                hilo.start()
                hilo.join()
                // Comprobamos si se ha podido registrar o no en el servidor
                if(hilo.isInstruccionRealizada){
                    Toast.makeText(this,R.string.a_Registrado,Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this,R.string.e_AnadirUsuario,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Introduce todos los campos",Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Método que se ejecuta cuando el radio button de terminos cambia su estado
         */
        rbTerminos.setOnCheckedChangeListener { buttonView, isChecked ->
            btnRegistrarse.isEnabled = isChecked
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZonaCompartida.eliminarActivity(this)
    }
}