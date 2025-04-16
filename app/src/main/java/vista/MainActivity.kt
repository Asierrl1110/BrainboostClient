package vista

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.clienteproyectofinal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val tbMenu = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tbMenu)


        val btnFlotante = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnFlotante.setOnClickListener(){
            val intent = Intent(this, MazoActivity::class.java)
            this.startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }
}