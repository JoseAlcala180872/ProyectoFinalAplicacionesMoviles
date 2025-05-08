package alcala.jose.personalhabits.ui.home

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.repositories.HabitRepository
import alcala.jose.personalhabits.repositories.UserRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val habitRepository = HabitRepository()
    val habitsLiveData: MutableLiveData<List<Habito>> = MutableLiveData()
    val totalHabitsOfTheDayLiveData = MutableLiveData<Int>()
    val totalDoneHabitsOfTheDayLiveData = MutableLiveData<Int>()
    fun fetchHabits() {
        habitRepository.getPendingHabitsForToday { habits ->
            habitsLiveData.value = habits
        }
    }

    fun getAmountHabitsOfTheDay() {
        habitRepository.getTotalHabitsScheduledForToday { count ->
            Log.d("HomeViewModel", "Total habits callback received: $count")
            totalHabitsOfTheDayLiveData.value = count // Use the actual count from the repository
        }
    }

    fun getDoneAmountHabitsOfTheDay() {
        habitRepository.getAmountDoneHabitsForToday { count ->
            Log.d("HomeViewModel", "Total done habits callback received: $count")
            totalDoneHabitsOfTheDayLiveData.value = count // Use the actual count from the repository
        }
    }



}