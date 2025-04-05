package alcala.jose.personalhabits.ui.home

import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.repositories.UserRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    val habitsLiveData: MutableLiveData<List<Habito>> = MutableLiveData()

    fun fetchHabits() {
        userRepository.getHabits { habits ->
            habitsLiveData.value = habits
        }
    }
}