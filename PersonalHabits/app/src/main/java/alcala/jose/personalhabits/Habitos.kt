package alcala.jose.personalhabits

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Habitos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_habitos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btnProgreso: Button = findViewById(R.id.progresoButton)
        btnProgreso.setOnClickListener {
            val intent = Intent(this, Progreso::class.java)
            startActivity(intent)
        }


        val btnAddHabit: ImageButton = findViewById(R.id.addHabitButton)
        btnAddHabit.setOnClickListener {
            val intent = Intent(this, AddHabito::class.java)
            startActivity(intent)
        }

    }
}