package alcala.jose.personalhabits

import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditHabito : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_habito)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras

        val nombre: EditText= findViewById(R.id.etNombreEdit)
        val descripcion: EditText= findViewById(R.id.etDescripcionEdit)
        val color: Spinner= findViewById(R.id.spColorEdit)
        val categoria: Spinner= findViewById(R.id.spCategoriaEdit)
        //val frecuencia: Spinner= findViewById(R.id.spFrecuenciaEdit)
        val hora: TextView= findViewById(R.id.tvHoraSeleccionadaEdit)


        nombre.setText(bundle!!.getString("habitName"))
        descripcion.setText(bundle!!.getString("habitDescription"))
        categoria.setSelection(bundle!!.getInt("habitCategory"))
        color.setSelection(bundle!!.getInt("habitColor"))
        hora.setText(bundle!!.getString("habitTime"))






    }
}