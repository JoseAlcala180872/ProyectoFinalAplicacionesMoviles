package alcala.jose.personalhabits.ui.dashboard

import alcala.jose.personalhabits.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import alcala.jose.personalhabits.databinding.FragmentDashboardBinding
import androidx.core.content.ContextCompat
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
            "Ejercicio" to Pair(4, 2),  // Pair(completed, uncompleted)
            "Estudio" to Pair(2, 5),
            "Salud" to Pair(3, 3),
            "Otro" to Pair(1, 6)
        )

        val monthlyData = mapOf(
            "Ejercicio" to Pair(12, 3),
            "Estudio" to Pair(9, 5),
            "Salud" to Pair(7, 4),
            "Otro" to Pair(4, 3)
        )

        setupBarChart(binding.barChartWeek, weeklyData, "Completados esta semana", "No completados esta semana")
        setupBarChart(binding.barChartMonth, monthlyData, "Completados este mes", "No completados este mes")
    }

    private fun setupBarChart(chart: BarChart, data: Map<String, Pair<Int, Int>>, completedLabel: String, uncompletedLabel: String) {
        val completedEntries = mutableListOf<BarEntry>()
        val uncompletedEntries = mutableListOf<BarEntry>()

        // Create two bars per category (completed and uncompleted)
        data.entries.forEachIndexed { index, entry ->
            completedEntries.add(BarEntry(index.toFloat(), entry.value.first.toFloat()))  // Completed
            uncompletedEntries.add(BarEntry(index.toFloat() + 0.3f, entry.value.second.toFloat()))  // Incompleted (shifted by 0.3 to be side-by-side)
        }

        // Create datasets for completed and uncompleted habits
        val completedDataSet = BarDataSet(completedEntries, completedLabel)
        completedDataSet.color = ContextCompat.getColor(requireContext(), R.color.veryPurple)

        val uncompletedDataSet = BarDataSet(uncompletedEntries, uncompletedLabel)
        uncompletedDataSet.color = ContextCompat.getColor(requireContext(), R.color.lessPurple)

        val barData = BarData(completedDataSet, uncompletedDataSet)

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

            // Adjust bar width and space between bars
            setScaleEnabled(false)  // Disable scaling to prevent zooming
            barData.barWidth = 0.3f  // Adjust the width of bars (optional)
            var groupSpace = 0.1f  // Adjust space between the groups of bars

            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


