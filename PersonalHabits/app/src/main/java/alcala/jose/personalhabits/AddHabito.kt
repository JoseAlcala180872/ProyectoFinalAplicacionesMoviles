package alcala.jose.personalhabits

import alcala.jose.personalhabits.ui.ColorPickerDialogFragment
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class AddHabito : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_habito)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btnAceptar: Button = findViewById(R.id.btnAceptar)
        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        val btnColor: ImageButton = findViewById(R.id.btnColorPicker)
        val etNombre: EditText = findViewById(R.id.etNombre)
        val etDescripcion: EditText = findViewById(R.id.etDescripcion)
        btnAceptar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            if (nombre.isEmpty()) {
                etNombre.error = "El nombre no puede estar vacío"
                return@setOnClickListener
            }

            val intent = Intent(this, Habitos::class.java)
            startActivity(intent)
            finish()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnColor.setOnClickListener {
            val dialog = ColorPickerDialogFragment()
            dialog.show(supportFragmentManager, "colorPickerDialog")
        }
    }
}