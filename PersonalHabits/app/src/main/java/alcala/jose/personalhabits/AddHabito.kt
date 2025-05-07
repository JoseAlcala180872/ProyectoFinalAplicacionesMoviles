package alcala.jose.personalhabits

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.repositories.CategoryRepository
import alcala.jose.personalhabits.repositories.HabitRepository
import alcala.jose.personalhabits.repositories.UserRepository
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.database.FirebaseDatabase

class AddHabito : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var llFrecuencia: LinearLayout
    private lateinit var ibHora: ImageButton
    private lateinit var tvHoraSeleccionada: TextView
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnColor: ImageButton
    private lateinit var spCategoria: Spinner
    private var selectedColor: Int = Color.GRAY
    private lateinit var habitRepository: HabitRepository
    private lateinit var userRepository: UserRepository
    private lateinit var categoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_habito)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        llFrecuencia = findViewById(R.id.llFrecuencia)
        ibHora = findViewById(R.id.ibHora)
        tvHoraSeleccionada = findViewById(R.id.tvHoraSeleccionada)
        spCategoria = findViewById(R.id.spCategoria)
        btnAceptar = findViewById(R.id.btnAceptar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnColor = findViewById(R.id.btnColorPicker)
        habitRepository = HabitRepository()
        userRepository = UserRepository()
        categoryRepository = CategoryRepository()

        setupCategoryDropdown()

        ibHora.setOnClickListener {
            showTimePickerDialog()
        }

        btnAceptar.setOnClickListener {
            guardarHabito()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnColor.setOnClickListener {
            val dialog = ColorPickerDialogFragment()
            dialog.show(supportFragmentManager, "colorPickerDialog")
        }

        supportFragmentManager.setFragmentResultListener("color_picked", this) { _, bundle ->
            val colorString = bundle.getString("color", "#777777")
            selectedColor = Color.parseColor(colorString)
            btnColor.setBackgroundColor(selectedColor)
        }

    }

    private fun setupCategoryDropdown() {
        // Get user categories and add default categories if not present
        userRepository.getCategories { userCategories ->
            val allCategories = mutableSetOf<String>()
            allCategories.addAll(userCategories)
            allCategories.addAll(categoryRepository.getCategories())

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                allCategories.toList()
            )
            spCategoria.adapter = adapter
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                tvHoraSeleccionada.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun guardarHabito() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val hora = tvHoraSeleccionada.text.toString()
        val diasSeleccionados = obtenerDiasSeleccionados()
        val categoria = spCategoria.selectedItem.toString()

        if (nombre.isEmpty() || descripcion.isEmpty() || hora.isEmpty() || diasSeleccionados.isEmpty() || categoria.isEmpty()) {
            etNombre.error = "Los campos no pueden estar vacíos"
            return
        }

        val nuevoHabito = Habito(
            nombre = nombre,
            descripcion = descripcion,
            hora = hora,
            frecuencia = diasSeleccionados,
            color = selectedColor,
            categoria = categoria,
            userId = userRepository.userId.toString()
        )

        habitRepository.addHabit(nuevoHabito) { success ->
            if (success) {
                Log.d("AddHabito", "Hábito guardado exitosamente")
                val intent = Intent(this, MenuFragment::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e("AddHabito", "Error al guardar el hábito")
            }
        }

        userRepository.getCategories { userCategories ->
            if (!userCategories.contains(categoria)) {
                userRepository.addCategory(categoria) {}
            }
        }
    }

    private fun obtenerDiasSeleccionados(): List<String> {
        val diasSeleccionados = mutableListOf<String>()
        for (i in 0 until llFrecuencia.childCount) {
            val checkBox = llFrecuencia.getChildAt(i) as? CheckBox
            if (checkBox?.isChecked == true) {
                diasSeleccionados.add(checkBox.text.toString())
            }
        }
        return diasSeleccionados
    }
}
