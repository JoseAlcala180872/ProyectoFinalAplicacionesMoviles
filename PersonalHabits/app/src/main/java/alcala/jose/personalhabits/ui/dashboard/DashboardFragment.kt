package alcala.jose.personalhabits.ui.dashboard

import alcala.jose.personalhabits.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import alcala.jose.personalhabits.databinding.FragmentDashboardBinding
import android.app.DatePickerDialog
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private var selectedCategory: String = ""
    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        setupDatePickers()
        setupCategorySelection()
        setupChartObservers()
        setupFilterButton()

        // Set default dates and load initial data
        val defaultDates = viewModel.getDefaultDates()
        startDate = defaultDates.first
        endDate = defaultDates.second

        binding.fechaInicioInput.setText(startDate)
        binding.fechaFinInput.setText(endDate)

        // Load initial data
        viewModel.loadStatsForDateRange(startDate, endDate, selectedCategory)

        return root
    }

    private fun setupDatePickers() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.fechaInicioInput.setOnClickListener {
            showDatePicker { year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                startDate = dateFormat.format(calendar.time)
                binding.fechaInicioInput.setText(startDate)
            }
        }

        binding.fechaFinInput.setOnClickListener {
            showDatePicker { year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                endDate = dateFormat.format(calendar.time)
                binding.fechaFinInput.setText(endDate)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day -> onDateSelected(year, month, day) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupCategorySelection() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoriesList = mutableListOf<String>()
            categoriesList.add("") // Empty option for "all categories"
            categoriesList.addAll(categories)

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, // Use simple_spinner_item for Spinner
                categoriesList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Set dropdown style

            binding.spCategoria.adapter = adapter // Use adapter property for Spinner
            binding.spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategory = categoriesList[position]
                    // Puedes agregar aquí alguna lógica adicional si necesitas reaccionar
                    // inmediatamente a la selección de la categoría.
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategory = "" // O algún valor por defecto si es necesario
                }
            }
        }

        viewModel.loadCategories()
    }

    private fun setupChartObservers() {
        viewModel.weeklyStats.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                setupBarChart(binding.barChartWeek, data, "Completados", "No completados")
            } else {
                // Clear chart if no data
                binding.barChartWeek.clear()
                binding.barChartWeek.invalidate()
            }
        }

        viewModel.monthlyStats.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                setupBarChart(binding.barChartMonth, data, "Completados", "No completados")
            } else {
                // Clear chart if no data
                binding.barChartMonth.clear()
                binding.barChartMonth.invalidate()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.confirmButton.isEnabled = !isLoading
            // You could also show a progress indicator here
        }
    }

    private fun setupFilterButton() {
        binding.confirmButton.setOnClickListener {
            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(context, "Por favor selecciona fechas válidas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            viewModel.loadStatsForDateRange(startDate, endDate, selectedCategory.ifEmpty { null })
        }
    }

    private fun setupBarChart(
        chart: BarChart,
        data: Map<String, Pair<Int, Int>>,
        completedLabel: String,
        uncompletedLabel: String
    ) {
        val completedEntries = mutableListOf<BarEntry>()
        val uncompletedEntries = mutableListOf<BarEntry>()
        val categories = data.keys.toList()

        data.entries.forEachIndexed { index, entry ->
            completedEntries.add(BarEntry(index.toFloat(), entry.value.first.toFloat()))
            uncompletedEntries.add(BarEntry(index.toFloat() + 0.3f, entry.value.second.toFloat()))
        }

        val completedDataSet = BarDataSet(completedEntries, completedLabel)
        completedDataSet.color = ContextCompat.getColor(requireContext(), R.color.veryPurple)

        val uncompletedDataSet = BarDataSet(uncompletedEntries, uncompletedLabel)
        uncompletedDataSet.color = ContextCompat.getColor(requireContext(), R.color.lessPurple)

        val barData = BarData(completedDataSet, uncompletedDataSet)

        chart.apply {
            this.data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(categories)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)

            setScaleEnabled(false)
            barData.barWidth = 0.3f

            // Adjust spacing if there are multiple categories
            if (categories.size > 1) {
                val groupSpace = 0.1f
                val barSpace = 0.05f

                // This ensures that the groups of bars are properly spaced
                groupBars(0f, groupSpace, barSpace)

                // Ensure the X axis fits all categories
                xAxis.axisMinimum = -0.5f
                xAxis.axisMaximum = categories.size - 0.5f
            }

            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}