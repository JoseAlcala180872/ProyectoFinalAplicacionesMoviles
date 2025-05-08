package alcala.jose.personalhabits.repositories

import alcala.jose.personalhabits.Dominio.Habito
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HabitRepository {

    private val habitRef = FirebaseDatabase.getInstance().getReference("habits")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun addHabit(habit: Habito, callback: (Boolean) -> Unit) {
        val habitId = habitRef.push().key ?: return
        val habitWithId = habit.copy(id = habitId, userId = userId.orEmpty())
        habitRef.child(habitId).setValue(habitWithId)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getHabits(callback: (List<Habito>) -> Unit) {
        if (userId == null) {
            callback(emptyList())
            return
        }

        Log.d("HabitRepository", "Fetching habits for user: $userId")
        habitRef.orderByChild("userId").equalTo(userId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val habitList = snapshot.children.mapNotNull {
                        it.getValue(Habito::class.java)
                    }
                    Log.d("HabitRepository", "Habits found: ${habitList.size}")
                    callback(habitList)
                } else {
                    Log.d("HabitRepository", "No habits found")
                    callback(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("HabitRepository", "Failed to fetch habits", e)
                callback(emptyList())
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

    // ✅ Returns count of habits scheduled for today (regardless of completion)
    fun getTotalHabitsScheduledForToday(callback: (Int) -> Unit) {
        val todayCode = getTodayCode()

        getHabits { habits ->
            val scheduled = habits.count { todayCode in (it.frecuencia ?: emptyList()) }
            Log.d("HabitRepository", "Total habits scheduled for today: $scheduled")
            callback(scheduled)
        }
    }

    fun getAmountDoneHabitsForToday(callback: (Int) -> Unit) {
        val currentDate = getCurrentDate()

        // Get the list of completed habit IDs for today (from daily_completions)
        UserRepository().getDoneHabits(currentDate) { completedHabitIds ->
            Log.d("HabitRepository", "Completed habits for today: ${completedHabitIds.size}")
            callback(completedHabitIds.size)
        }
    }


    fun getPendingHabitsForToday(callback: (List<Habito>) -> Unit) {
        val currentDate = getCurrentDate()
        val todayCode = UserRepository().getDayCodeFromDate(currentDate)

        UserRepository().getDoneHabits(currentDate) { completedIds ->
            getHabits { habits ->
                val pending = habits.filter {
                    todayCode in (it.frecuencia ?: emptyList()) &&
                            !completedIds.contains(it.id)
                }
                Log.d("HabitRepository", "Pending habit list size: ${pending.size}")
                callback(pending)
            }
        }
    }

    // 🛠️ Utility methods
    private fun getCurrentDate(): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(Date())
    }

    private fun getTodayCode(): String {
        return UserRepository().getDayCodeFromDate(getCurrentDate())
    }
}
