package alcala.jose.personalhabits.ui.charts

import alcala.jose.personalhabits.Charts.ChartDTO
import alcala.jose.personalhabits.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import alcala.jose.personalhabits.databinding.FragmentDashboardBinding
import alcala.jose.personalhabits.ui.ChartAdapter
import android.app.DatePickerDialog
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class ChartsFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var chartAdapter: ChartAdapter
    private lateinit var weeklyChartAdapter: ChartAdapter
    private lateinit var viewModel: ChartsViewModel
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

        viewModel = ViewModelProvider(this).get(ChartsViewModel::class.java)

        setupDatePickers()
        setupCategorySelection()
        setupChartObservers()
        setupFilterButton()

        // Monthly recyclerView
        binding.chartysRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chartAdapter = ChartAdapter(requireContext(), ArrayList(), "")
        binding.chartysRecyclerView.adapter = chartAdapter

        // Weekly recyclerView
        binding.WeeklychartysRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        weeklyChartAdapter = ChartAdapter(requireContext(), ArrayList(), "")
        binding.WeeklychartysRecyclerView.adapter = weeklyChartAdapter

        // Set default dates and show them
        val defaultDates = viewModel.getDefaultDates()
        startDate = defaultDates.first
        endDate = defaultDates.second

        viewModel.loadWeeklyStats(startDate, endDate, selectedCategory,requireContext())

        // Month spinner
        val monthSpinner: Spinner = root.findViewById(R.id.monthSpinner)
        val months = DateFormatSymbols().months.take(12)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter
        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH))
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = position // ✅ This is an Int from 0 (Jan) to 11 (Dec)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                viewModel.loadMonthlyStats(selectedMonth, currentYear, requireContext())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
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
            val categoriesList = mutableListOf("").apply { addAll(categories) }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoriesList
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            binding.spCategoria.adapter = adapter
            binding.spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategory = categoriesList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategory = ""
                }
            }
        }

        viewModel.loadCategories()
    }

    private fun setupChartObservers() {
        viewModel.weeklyStats.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                weeklyChartAdapter.updateData(data as ArrayList<ChartDTO?>?)
            }
        }
        viewModel.monthlyStats.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                chartAdapter.updateData(data as ArrayList<ChartDTO?>?)
            }
        }
    }

    private fun setupFilterButton() {
        binding.confirmButton.setOnClickListener {
            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(context, "Por favor selecciona fechas válidas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.loadWeeklyStats(startDate, endDate, selectedCategory, requireContext())
            weeklyChartAdapter.applyFilter(selectedCategory)

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
