package alcala.jose.personalhabits.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import alcala.jose.personalhabits.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupMockCharts()

        return root
    }

    private fun setupMockCharts() {
        // Mock habit completion counts by category
        val weeklyData = mapOf(
            "Ejercicio" to 4,
            "Estudio" to 2,
            "Salud" to 3,
            "Otro" to 1
        )

        val monthlyData = mapOf(
            "Ejercicio" to 12,
            "Estudio" to 9,
            "Salud" to 7,
            "Otro" to 4
        )

        setupBarChart(binding.barChartWeek, weeklyData, "Completados esta semana")
        setupBarChart(binding.barChartMonth, monthlyData, "Completados este mes")
    }

    private fun setupBarChart(chart: BarChart, data: Map<String, Int>, label: String) {
        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, label)
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(dataSet)

        chart.apply {
            this.data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(data.keys.toList())
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
