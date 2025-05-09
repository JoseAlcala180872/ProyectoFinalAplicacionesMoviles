package alcala.jose.personalhabits.ui.charts

import alcala.jose.personalhabits.Charts.ChartDTO
import alcala.jose.personalhabits.Repositories.CategoryRepository
import alcala.jose.personalhabits.Repositories.HabitRepository
import alcala.jose.personalhabits.Repositories.UserRepository
import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ChartsViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()
    private val categoryRepository = CategoryRepository()

    private val _weeklyStats = MutableLiveData<List<ChartDTO>>()
    val weeklyStats: LiveData<List<ChartDTO>> = _weeklyStats

    private val _monthlyStats = MutableLiveData<List<ChartDTO>>()
    val monthlyStats: LiveData<List<ChartDTO>> =_monthlyStats

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCategories() {
        // Directly retrieve categories without a callback
        _categories.postValue(userRepository.getCategories())
    }

    fun getDefaultDates(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val endDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val startDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    fun loadWeeklyStats(startDate: String, endDate: String, category: String, context: Context) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val result = habitRepository.getCategoryStats(startDate, endDate, context)
                _weeklyStats.postValue(result)
            } catch (e: Exception) {
            } finally {
                _isLoading.postValue(false)
            }
        }
    }



    fun getMonthDateRange(month: Int, year: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(calendar.time)

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDay)
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    fun loadMonthlyStats(month: Int, year: Int, context: Context) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val (startDate, endDate) = getMonthDateRange(month, year)
                val result = habitRepository.getCategoryStats(startDate, endDate, context)
                _monthlyStats.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }



}
