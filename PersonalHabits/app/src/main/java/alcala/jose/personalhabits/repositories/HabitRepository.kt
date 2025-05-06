package alcala.jose.personalhabits.repositories

import alcala.jose.personalhabits.Dominio.Habito
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HabitRepository() {

    private val habitRef = FirebaseDatabase.getInstance().getReference("habits")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun addHabit(habit: Habito, callback: (Boolean) -> Unit) {
        val habitId = habitRef.push().key ?: return
        val habitWithId = habit.copy(id = habitId, userId = userId.toString())
        habitRef.child(habitId).setValue(habitWithId)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getHabits(callback: (List<Habito>) -> Unit) {
        if (userId == null) {
            callback(ArrayList())
            return
        }

        habitRef.orderByChild("userId").equalTo(userId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val habitList = snapshot.children.mapNotNull { it.getValue(Habito::class.java) }
                    callback(ArrayList(habitList))
                } else {
                    callback(ArrayList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Failed to fetch habits", e)
                callback(ArrayList()) // Return an empty ArrayList on failure
            }
    }


    fun updateHabit(habit: Habito, callback: (Boolean) -> Unit) {
        habitRef.child(habit.id).setValue(habit)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deleteHabit(habitId: String, callback: (Boolean) -> Unit) {
        habitRef.child(habitId).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getPendingHabitsForToday(callback: (List<Habito>) -> Unit) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val todayCode = UserRepository().getDayCodeFromDate(currentDate)

        UserRepository().getDoneHabits(currentDate) { completedHabitIds ->
            getHabits { habits ->
                val pending = habits.filter {
                    todayCode in (it.frecuencia ?: emptyList()) &&
                            !completedHabitIds.contains(it.id)
                }
                callback(pending)
            }
        }
    }
}
