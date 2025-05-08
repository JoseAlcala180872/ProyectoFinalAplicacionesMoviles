package alcala.jose.personalhabits.ui.dashboard

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.repositories.CategoryRepository
import alcala.jose.personalhabits.repositories.HabitRepository
import alcala.jose.personalhabits.repositories.UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()

    private val _weeklyStats = MutableLiveData<Map<String, Pair<Int, Int>>>()
    val weeklyStats: LiveData<Map<String, Pair<Int, Int>>> = _weeklyStats

    private val _monthlyStats = MutableLiveData<Map<String, Pair<Int, Int>>>()
    val monthlyStats: LiveData<Map<String, Pair<Int, Int>>> = _monthlyStats

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCategories() {
        userRepository.getCategories { categories ->
            _categories.postValue(categories)
        }
    }

    fun loadStatsForDateRange(startDate: String, endDate: String, category: String? = null) {
        _isLoading.postValue(true)

        // Get all habits
        habitRepository.getHabits { habits ->
            // Filter by category if specified
            val filteredHabits = if (category != null && category.isNotEmpty()) {
                habits.filter { it.categoria == category }
            } else {
                habits
            }

            if (filteredHabits.isEmpty()) {
                _weeklyStats.postValue(emptyMap())
                _monthlyStats.postValue(emptyMap())
                _isLoading.postValue(false)
                return@getHabits
            }

            // Process stats by category
            processStatsForDateRange(filteredHabits, startDate, endDate)
        }
    }

    private fun processStatsForDateRange(habits: List<Habito>, startDateStr: String, endDateStr: String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        try {
            val startDate = dateFormat.parse(startDateStr) ?: Date()
            val endDate = dateFormat.parse(endDateStr) ?: Date()

            // Create a calendar for iteration
            val calendar = Calendar.getInstance()
            calendar.time = startDate

            // Create maps to store weekly and monthly data
            val weeklyData = mutableMapOf<String, MutableMap<String, Pair<Int, Int>>>()
            val monthlyData = mutableMapOf<String, MutableMap<String, Pair<Int, Int>>>()

            // Prepare category maps
            habits.forEach { habit ->
                val category = habit.categoria ?: "Sin categoría"

                if (!weeklyData.containsKey(category)) {
                    weeklyData[category] = mutableMapOf()
                }

                if (!monthlyData.containsKey(category)) {
                    monthlyData[category] = mutableMapOf()
                }
            }

            // Iterate through each day in the range
            while (!calendar.time.after(endDate)) {
                val currentDate = dateFormat.format(calendar.time)
                val currentDayCode = userRepository.getDayCodeFromDate(currentDate)

                // Get completed habits for current date
                userRepository.getDoneHabits(currentDate) { completedHabitIds ->
                    // Process each habit for this date
                    habits.forEach { habit ->
                        val category = habit.categoria ?: "Sin categoría"
                        val isScheduledForToday = currentDayCode in (habit.frecuencia ?: emptyList())

                        if (isScheduledForToday) {
                            val isCompleted = completedHabitIds.contains(habit.id)

                            // Update weekly data
                            val weekStats = weeklyData[category]!![currentDate] ?: Pair(0, 0)
                            if (isCompleted) {
                                weeklyData[category]!![currentDate] = Pair(weekStats.first + 1, weekStats.second)
                            } else {
                                weeklyData[category]!![currentDate] = Pair(weekStats.first, weekStats.second + 1)
                            }

                            // Update monthly data
                            val calMonth = Calendar.getInstance()
                            calMonth.time = calendar.time
                            val monthYear = "${calMonth.get(Calendar.MONTH)}-${calMonth.get(Calendar.YEAR)}"

                            val monthStats = monthlyData[category]!![monthYear] ?: Pair(0, 0)
                            if (isCompleted) {
                                monthlyData[category]!![monthYear] = Pair(monthStats.first + 1, monthStats.second)
                            } else {
                                monthlyData[category]!![monthYear] = Pair(monthStats.first, monthStats.second + 1)
                            }
                        }
                    }

                    // Aggregate weekly data
                    val aggregatedWeeklyData = weeklyData.mapValues { (_, dateMap) ->
                        var completed = 0
                        var uncompleted = 0

                        dateMap.values.forEach { pair ->
                            completed += pair.first
                            uncompleted += pair.second
                        }

                        Pair(completed, uncompleted)
                    }

                    // Aggregate monthly data
                    val aggregatedMonthlyData = monthlyData.mapValues { (_, monthMap) ->
                        var completed = 0
                        var uncompleted = 0

                        monthMap.values.forEach { pair ->
                            completed += pair.first
                            uncompleted += pair.second
                        }

                        Pair(completed, uncompleted)
                    }

                    _weeklyStats.postValue(aggregatedWeeklyData)
                    _monthlyStats.postValue(aggregatedMonthlyData)
                    _isLoading.postValue(false)
                }

                // Move to the next day
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        } catch (e: Exception) {
            _isLoading.postValue(false)
            // Handle parsing errors
            _weeklyStats.postValue(emptyMap())
            _monthlyStats.postValue(emptyMap())
        }
    }

    fun getDefaultDates(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Default end date is today
        val endDate = dateFormat.format(calendar.time)

        // Default start date is 7 days ago for weekly view
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val startDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }
}