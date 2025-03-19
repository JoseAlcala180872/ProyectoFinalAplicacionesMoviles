import alcala.jose.personalhabits.AddHabito
import alcala.jose.personalhabits.HabitAdapter
import alcala.jose.personalhabits.Habito
import alcala.jose.personalhabits.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Habitos : AppCompatActivity() {

    private lateinit var habitAdapter: HabitAdapter
    private val habitList = mutableListOf<Habito>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habitos)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        habitAdapter = HabitAdapter(this, habitList,
            onEditClick = { habit ->
                val intent = Intent(this, AddHabito::class.java)
                intent.putExtra("HABITO_ID", habit.id)
                intent.putExtra("NOMBRE", habit.nombre)
                intent.putExtra("CATEGORIA", habit.categoria)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                habitList.remove(habit)
                habitAdapter.notifyDataSetChanged()
            },
            onCompleteClick = { habit -> // Manejar completar
                habit.completado = true
                habitAdapter.notifyDataSetChanged()  // Refrescar la vista
            }
        )

        recyclerView.adapter = habitAdapter

        // Datos de prueba
        habitList.add(Habito(1, "Leer", "Educación"))
        habitList.add(Habito(2, "Ejercicio", "Salud"))
        habitAdapter.notifyDataSetChanged()
    }
}

