package alcala.jose.personalhabits.Repositories

import alcala.jose.personalhabits.Charts.ChartDTO
import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.R
import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class HabitRepository {

    private val habitRef = FirebaseDatabase.getInstance().getReference("habits")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userRef = FirebaseDatabase.getInstance()
        .getReference("users")
        .child(userId)
    val dailyCompletionRef = userRef.child("daily_completions")

    suspend fun addHabit(habit: Habito): Boolean {
        return try {
            val habitId = habitRef.push().key ?: return false
            val habitWithId = habit.copy(id = habitId, userId = userId)
            habitRef.child(habitId).setValue(habitWithId).await()
            true
        } catch (e: Exception) {
            Log.e("HabitRepository", "Failed to add habit", e)
            false
        }
    }

    suspend fun getHabits(): List<Habito> {
        if (userId.isBlank()) return emptyList()
        return try {
            Log.d("HabitRepository", "Fetching user habits")
            val snapshot = habitRef.orderByChild("userId").equalTo(userId).get().await()
            snapshot.children.mapNotNull { it.getValue(Habito::class.java) }
        } catch (e: Exception) {
            Log.e("HabitRepository", "Failed to fetch habits", e)
            emptyList()
        }
    }

    fun getStreakAndLastCompletionDate(callback: (streak: Int, lastCompletionDate: String?) -> Unit) {
        if (userId.isBlank()) {
            callback(0, null)
            return
        }

        userRef.get().addOnSuccessListener { snapshot ->
            val lastCompletionDate = snapshot.child("lastCompletionDate").getValue(String::class.java)
            val streak = snapshot.child("streak").getValue(Int::class.java) ?: 0
            callback(streak, lastCompletionDate)
        }.addOnFailureListener {
            callback(0, null)
        }
    }

    fun updateStreakAndCompletionDate(callback: (Boolean) -> Unit) {
        if (userId.isBlank()) {
            callback(false)
            return
        }

        getStreakAndLastCompletionDate { currentStreak, lastDate ->
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val today = dateFormat.format(Date())

            var newStreak = currentStreak
            val isSameDay = lastDate == today
            val isNextDay = try {
                val last = dateFormat.parse(lastDate ?: "")
                val now = dateFormat.parse(today)
                val diff = (now.time - (last?.time ?: 0)) / (1000 * 60 * 60 * 24)
                diff == 1L
            } catch (_: Exception) { false }

            newStreak = when {
                isSameDay -> currentStreak
                isNextDay -> currentStreak + 1
                else -> 1
            }

            val updates = mapOf(
                "lastCompletionDate" to today,
                "streak" to newStreak
            )

            userRef.updateChildren(updates).addOnCompleteListener {
                callback(it.isSuccessful)
            }
        }
    }

    fun updateCompletionStatus(habitId: String, completed: Boolean, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        updateStreakAndCompletionDate(
        ) { success ->
            if (success) {
                Log.d("UpdateStreak", "Streak updated successfully")
            } else {
                Log.e("UpdateStreak", "Failed to update streak")
            }
        }
        if (habitId.isBlank() || userId.isBlank()) {
            callback(false)
            return
        }

        val dailyCompletionRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("daily_completions")

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        try {
            val habitCompletionRef = dailyCompletionRef.child(currentDate).child(habitId)
            habitCompletionRef.setValue(if (completed) true else null).addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
        } catch (e: Exception) {
            Log.e("HabitRepository", "Failed to update completion status for habit $habitId", e)
            callback(false)
        }
    }


    suspend fun updateHabit(habit: Habito): Boolean {
        val id = habit.id
        if (id.isNullOrBlank()) return false
        return try {
            habitRef.child(id).setValue(habit).await()
            true
        } catch (e: Exception) {
            Log.e("HabitRepository", "Failed to update habit", e)
            false
        }
    }

    fun deleteHabit(habitId: String, callback: (Boolean) -> Unit) {
        if (habitId.isBlank()) {
            callback(false) // Return false if habitId is blank
            return
        }

        try {
            habitRef.child(habitId).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true) // Success callback
                } else {
                    callback(false) // Failure callback
                }
            }
        } catch (e: Exception) {
            Log.e("HabitRepository", "Failed to delete habit", e)
            callback(false) // Error callback
        }
    }


    suspend fun getTotalHabitsScheduledForToday(): Int {
        val todayCode = getTodayCode()
        val habits = getHabits()
        val scheduled = habits.count { todayCode in (it.frecuencia ?: emptyList()) }
        Log.d("HabitRepository", "Habits scheduled for today: $scheduled")
        return scheduled
    }

    suspend fun getAmountDoneHabitsForToday(): Int {
        val currentDate = getCurrentDate()
        val completed = getDoneHabits(currentDate)
        return completed.size
    }

    suspend fun getDoneHabits(date: String): List<String> {
        if (userId.isBlank()) return emptyList()
        return try {
            val snapshot = dailyCompletionRef.child(date).get().await()
            snapshot.children.mapNotNull { it.key }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching done habits for $date", e)
            emptyList()
        }
    }


    fun getDayCodeFromDate(dateString: String): String {
        return try {
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            val calendar = Calendar.getInstance().apply { time = date ?: Date() }
            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY    -> "L"
                Calendar.TUESDAY   -> "M"
                Calendar.WEDNESDAY -> "MX"
                Calendar.THURSDAY  -> "J"
                Calendar.FRIDAY    -> "V"
                Calendar.SATURDAY  -> "S"
                Calendar.SUNDAY    -> "D"
                else -> ""
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Invalid date string: $dateString", e)
            ""
        }
    }

    suspend fun getPendingHabitsForToday(): List<Habito> {
        val currentDate = getCurrentDate()
        val todayCode = getDayCodeFromDate(currentDate)
        val completedIds = getDoneHabits(currentDate)
        val habits = getHabits()

        val pending = habits.filter {
            todayCode in (it.frecuencia ?: emptyList()) &&
                    it.id != null && !completedIds.contains(it.id)
        }

        Log.d("HabitRepository", "Pending habits today: ${pending.size}")
        return pending
    }

    suspend fun getCategoryStats(
        startDate: String,
        endDate: String,
        context: Context
    ): List<ChartDTO> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val start = dateFormat.parse(startDate) ?: return emptyList()
        val end = dateFormat.parse(endDate) ?: return emptyList()

        val allHabits = getHabits()
        val grouped = allHabits.groupBy { it.categoria }

        val result = mutableListOf<ChartDTO>()

        for ((category, habits) in grouped) {
            var total = 0
            var done = 0
            calendar.time = start
            while (!calendar.time.after(end)) {
                val currentDate = dateFormat.format(calendar.time)
                val dayCode = getDayCodeFromDate(currentDate)

                val scheduledHabits = habits.filter { habit ->
                    dayCode in (habit.frecuencia ?: emptyList())
                }

                val completedIds = getDoneHabits(currentDate)

                total += scheduledHabits.size
                done += scheduledHabits.count { it.id in completedIds }

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            if (total > 0) {
                val porcentaje = (done.toFloat() / total) * 100
                result.add(
                    ChartDTO(
                        label = category,
                        porcentaje = porcentaje,
                        color = "#FF5733".toColorInt(),
                        number = done.toString()
                    )
                )
            }
        }

        return result
    }

    private fun getCurrentDate(): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(Date())
    }

    private fun getTodayCode(): String {
        return getDayCodeFromDate(getCurrentDate())
    }
}
