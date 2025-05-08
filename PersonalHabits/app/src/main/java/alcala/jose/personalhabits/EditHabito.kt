package alcala.jose.personalhabits

import alcala.jose.personalhabits.repositories.CategoryRepository
import alcala.jose.personalhabits.repositories.HabitRepository
import alcala.jose.personalhabits.repositories.UserRepository
import android.graphics.Color
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.ArrayAdapter
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
private lateinit var userRepository: UserRepository
private lateinit var categoryRepository: CategoryRepository

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

        userRepository = UserRepository()
        categoryRepository = CategoryRepository()

        // Get data from intent
        val bundle = intent.extras
        val nombre: EditText = findViewById(R.id.etNombreEdit)
        val descripcion: EditText = findViewById(R.id.etDescripcionEdit)
        val color: Spinner = findViewById(R.id.spColorEdit)
        val cbLunesEdit: CheckBox = findViewById(R.id.cbLunesEdit)
        val cbMartesEdit: CheckBox = findViewById(R.id.cbMartesEdit)
        val cbMiercolesEdit: CheckBox = findViewById(R.id.cbMiercolesEdit)
        val cbJuevesEdit: CheckBox = findViewById(R.id.cbJuevesEdit)
        val cbViernesEdit: CheckBox = findViewById(R.id.cbViernesEdit)
        val cbSabadoEdit: CheckBox = findViewById(R.id.cbSabadoEdit)
        val cbDomingoEdit: CheckBox = findViewById(R.id.cbDomingoEdit)
        val hora: TextView = findViewById(R.id.tvHoraSeleccionadaEdit)

        nombre.setText(bundle!!.getString("habitName"))
        descripcion.setText(bundle!!.getString("habitDescription"))
        val colorInt = bundle!!.getInt("habitColor")
        val btnColorPickerEdit: ImageButton = findViewById(R.id.btnColorPickerEdit)

        btnColorPickerEdit.setBackgroundColor(colorInt)

        hora.setText(bundle!!.getString("habitTime"))

        val frecuenciaList = bundle!!.getStringArrayList("habitFrequency") ?: arrayListOf()
        cbLunesEdit.isChecked = frecuenciaList.contains("L")
        cbMartesEdit.isChecked = frecuenciaList.contains("M")
        cbMiercolesEdit.isChecked = frecuenciaList.contains("X")
        cbJuevesEdit.isChecked = frecuenciaList.contains("J")
        cbViernesEdit.isChecked = frecuenciaList.contains("V")
        cbSabadoEdit.isChecked = frecuenciaList.contains("S")
        cbDomingoEdit.isChecked = frecuenciaList.contains("D")

        userRepository.getCategories { userCategories ->
            val allCategories = mutableSetOf<String>()
            allCategories.addAll(userCategories)
            allCategories.addAll(categoryRepository.getCategories())

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                allCategories.toList()
            )
            val categoriaSpinner: Spinner = findViewById(R.id.spCategoriaEdit)
            categoriaSpinner.adapter = adapter

            val categoriaValue = intent.getStringExtra("habitCategory") ?: ""
            val categoriaIndex = (0 until categoriaSpinner.count).firstOrNull {
                categoriaSpinner.getItemAtPosition(it).toString() == categoriaValue
            } ?: 0

            categoriaSpinner.setSelection(categoriaIndex)
        }

    }


}
