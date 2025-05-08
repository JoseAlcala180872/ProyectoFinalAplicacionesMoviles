package alcala.jose.personalhabits.ui.home

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.repositories.HabitRepository
import alcala.jose.personalhabits.repositories.UserRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()
    val habitsLiveData: MutableLiveData<List<Habito>> = MutableLiveData()

    fun fetchHabits() {
        habitRepository.getPendingHabitsForToday { habits ->
            habitsLiveData.value = habits
        }
    }
}