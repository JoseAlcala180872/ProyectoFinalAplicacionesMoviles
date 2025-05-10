import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.Repositories.HabitRepository
import alcala.jose.personalhabits.Repositories.UserRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()

    val habitsLiveData: MutableLiveData<List<Habito>> = MutableLiveData()
    val totalHabitsOfTheDayLiveData = MutableLiveData<Int>()
    val totalDoneHabitsOfTheDayLiveData = MutableLiveData<Int>()

    fun fetchHabits() {
        viewModelScope.launch {
            val habits = habitRepository.getPendingHabitsForToday()
            Log.d("HabitsViewModel", "Fetched ${habits.size} habits")
            habitsLiveData.value = habits
            getAmountHabitsOfTheDay()
            getDoneAmountHabitsOfTheDay()
        }
    }

    fun getAmountHabitsOfTheDay() {
        viewModelScope.launch {
            val count = habitRepository.getTotalHabitsScheduledForToday()
            Log.d("HomeViewModel", "Total habits callback received: $count")
            totalHabitsOfTheDayLiveData.value = count
        }
    }

    fun getDoneAmountHabitsOfTheDay() {
        viewModelScope.launch {
            val count = habitRepository.getAmountDoneHabitsForToday()
            Log.d("HomeViewModel", "Total done habits callback received: $count")
            totalDoneHabitsOfTheDayLiveData.value = count
        }
    }
}
