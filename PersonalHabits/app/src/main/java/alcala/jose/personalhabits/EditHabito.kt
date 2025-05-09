package alcala.jose.personalhabits

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.Repositories.CategoryRepository
import alcala.jose.personalhabits.Repositories.HabitRepository
import alcala.jose.personalhabits.Repositories.UserRepository
import android.app.TimePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.ArrayAdapter
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditHabito : AppCompatActivity() {

    // Use the viewModels property delegate to ensure proper lifecycle management
    private val userRepository: UserRepository by lazy { UserRepository() }
    private val categoryRepository: CategoryRepository by lazy { CategoryRepository() }
    private val habitRepository: HabitRepository by lazy { HabitRepository() }

    private lateinit var colorPreviewEdit: View
    private var selectedColor: Int = Color.GRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_habito)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get data from intent
        val bundle = intent.extras
        val nombre: EditText = findViewById(R.id.etNombreEdit)
        val descripcion: EditText = findViewById(R.id.etDescripcionEdit)
        val cbLunesEdit: CheckBox = findViewById(R.id.cbLunesEdit)
        val cbMartesEdit: CheckBox = findViewById(R.id.cbMartesEdit)
        val cbMiercolesEdit: CheckBox = findViewById(R.id.cbMiercolesEdit)
        val cbJuevesEdit: CheckBox = findViewById(R.id.cbJuevesEdit)
        val cbViernesEdit: CheckBox = findViewById(R.id.cbViernesEdit)
        val cbSabadoEdit: CheckBox = findViewById(R.id.cbSabadoEdit)
        val cbDomingoEdit: CheckBox = findViewById(R.id.cbDomingoEdit)
        val hora: TextView = findViewById(R.id.tvHoraSeleccionadaEdit)
        val ibHoraEdit: ImageButton = findViewById(R.id.ibHoraEdit)
        val btnColorPickerEdit: ImageButton = findViewById(R.id.btnColorPickerEdit)
        colorPreviewEdit = findViewById(R.id.colorPreviewEdit)
        var btnCancelar: Button = findViewById(R.id.btnCancelarEdit)
        var btnAceptar: Button = findViewById(R.id.btnAceptarEdit)

        ibHoraEdit.setOnClickListener {
            showTimePickerDialog()
        }

        btnAceptar.setOnClickListener {
            updateHabito()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnColorPickerEdit.setOnClickListener {
            val dialog = ColorPickerDialogFragment()
            dialog.show(supportFragmentManager, "colorPickerDialog")
        }

        supportFragmentManager.setFragmentResultListener("color_picked", this) { _, bundle ->
            val colorString = bundle.getString("color", "#777777")
            selectedColor = Color.parseColor(colorString)
            colorPreviewEdit.setBackgroundColor(selectedColor)
        }

        nombre.setText(bundle!!.getString("habitName"))
        descripcion.setText(bundle!!.getString("habitDescription"))
        val colorInt = bundle!!.getInt("habitColor")
        selectedColor = colorInt
        colorPreviewEdit.setBackgroundColor(colorInt)

        hora.setText(bundle!!.getString("habitTime"))

        val frecuenciaList = bundle!!.getStringArrayList("habitFrequency") ?: arrayListOf()
        cbLunesEdit.isChecked = frecuenciaList.contains("L")
        cbMartesEdit.isChecked = frecuenciaList.contains("M")
        cbMiercolesEdit.isChecked = frecuenciaList.contains("X")
        cbJuevesEdit.isChecked = frecuenciaList.contains("J")
        cbViernesEdit.isChecked = frecuenciaList.contains("V")
        cbSabadoEdit.isChecked = frecuenciaList.contains("S")
        cbDomingoEdit.isChecked = frecuenciaList.contains("D")

        // Using repository to fetch categories
        val allCategories = categoryRepository.getCategories()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            allCategories
        )
        val categoriaSpinner: Spinner = findViewById(R.id.spCategoriaEdit)
        categoriaSpinner.adapter = adapter

        val categoriaValue = intent.getStringExtra("habitCategory") ?: ""
        val categoriaIndex = (0 until categoriaSpinner.count).firstOrNull {
            categoriaSpinner.getItemAtPosition(it).toString() == categoriaValue
        } ?: 0

        categoriaSpinner.setSelection(categoriaIndex)

    }

    private fun updateHabito() {
        val nombre: EditText = findViewById(R.id.etNombreEdit)
        val descripcion: EditText = findViewById(R.id.etDescripcionEdit)
        val hora: TextView = findViewById(R.id.tvHoraSeleccionadaEdit)
        val categoriaSpinner: Spinner = findViewById(R.id.spCategoriaEdit)

        val habitName = nombre.text.toString().trim()
        val habitDescription = descripcion.text.toString().trim()
        val habitTime = hora.text.toString().trim()
        val habitCategory = categoriaSpinner.selectedItem.toString()
        val habitFrequency = obtenerDiasSeleccionados()

        // Validation
        if (habitName.isEmpty() || habitTime.isEmpty()) {
            Log.e("EditHabito", "Name or time is empty")
            return
        }

        // Get habit ID and user ID from intent (or however you’re passing it)
        val habitId = intent.getStringExtra("habitId")
        val userId = intent.getStringExtra("userId")

        if (habitId == null || userId == null) {
            Log.e("EditHabito", "Missing habitId or userId")
            return
        }

        val updatedHabit = Habito(
            id = habitId,
            nombre = habitName,
            descripcion = habitDescription,
            hora = habitTime,
            categoria = habitCategory,
            frecuencia = habitFrequency,
            color = selectedColor,
            userId = userId
        )

        // Inside your Activity or Fragment
        lifecycleScope.launch {
            val success = try {
                habitRepository.updateHabit(updatedHabit)
            } catch (e: Exception) {
                Log.e("EditHabito", "Failed to update habit", e)
                false
            }

            if (success) {
                Log.d("EditHabito", "Habit updated successfully")
                finish()
            } else {
                Log.e("EditHabito", "Failed to update habit")
            }
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            R.style.TimePickerTheme,
            { _, selectedHour, selectedMinute ->
                val horaSeleccionada: TextView = findViewById(R.id.tvHoraSeleccionadaEdit)
                horaSeleccionada.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun obtenerDiasSeleccionados(): List<String> {
        val llFrecuencia: LinearLayout = findViewById(R.id.llFrecuenciaEdit)
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
