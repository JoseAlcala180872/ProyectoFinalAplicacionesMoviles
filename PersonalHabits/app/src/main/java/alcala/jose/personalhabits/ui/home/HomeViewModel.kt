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

    // Fetch pending habits for the day
    fun fetchHabits() {
        viewModelScope.launch {
            val habits = habitRepository.getPendingHabitsForToday()
            habitsLiveData.value = habits
        }
    }



    // Get the total number of habits scheduled for today
    fun getAmountHabitsOfTheDay() {
        viewModelScope.launch {
            val count = habitRepository.getTotalHabitsScheduledForToday()
            Log.d("HomeViewModel", "Total habits callback received: $count")
            totalHabitsOfTheDayLiveData.value = count
        }
    }

    // Get the total number of done habits for today
    fun getDoneAmountHabitsOfTheDay() {
        viewModelScope.launch {
            val count = habitRepository.getAmountDoneHabitsForToday()
            Log.d("HomeViewModel", "Total done habits callback received: $count")
            totalDoneHabitsOfTheDayLiveData.value = count
        }
    }
}
