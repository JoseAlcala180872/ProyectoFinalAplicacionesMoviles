package alcala.jose.personalhabits.ui.habits

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.Repositories.HabitRepository
import alcala.jose.personalhabits.Repositories.UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()
    val habitsLiveData: MutableLiveData<List<Habito>> = MutableLiveData()
    private val _streak = MutableLiveData<Int>()
    val streak: LiveData<Int> get() = _streak

    fun loadStreak() {
        habitRepository.getStreakAndLastCompletionDate { streak, lastCompletionDate ->
            _streak.postValue(streak)
            // Optionally, you can also store the lastCompletionDate for other use cases.
        }
    }

    fun fetchHabits() {
        viewModelScope.launch {
            val habits = habitRepository.getHabits()
            habitsLiveData.postValue(habits)
        }
    }

}
