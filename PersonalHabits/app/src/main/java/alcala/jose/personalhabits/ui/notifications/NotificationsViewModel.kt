package alcala.jose.personalhabits.ui.notifications

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.repositories.HabitRepository
import alcala.jose.personalhabits.repositories.UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()
    val habitsLiveData: MutableLiveData<List<Habito>> = MutableLiveData()
    private val _streak = MutableLiveData<Int>()
    val streak: LiveData<Int> get() = _streak

    fun loadStreak() {
        userRepository.getStreak { result ->
            _streak.postValue(result)
        }
    }
    fun fetchHabits() {
        habitRepository.getHabits { habits ->
            habitsLiveData.value = habits
        }
    }
}
