package alcala.jose.personalhabits

import alcala.jose.personalhabits.ui.ColorPickerDialogFragment
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        btnAceptar.setOnClickListener {
            val intent = Intent(this, Habitos::class.java)
            startActivity(intent)
            finish()
        }

        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this, Habitos::class.java)
            startActivity(intent)
            finish()
        }

        val btnColor: ImageButton  = findViewById(R.id.btnColorPicker)
        btnAceptar.setOnClickListener {
            val dialog = ColorPickerDialogFragment()
            dialog.show(supportFragmentManager, "colorPickerDialog")


    }
    }
}