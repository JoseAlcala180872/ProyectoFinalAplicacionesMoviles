package alcala.jose.personalhabits.repositories

import alcala.jose.personalhabits.Dominio.Habito
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UserRepository {

    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun getPendingHabitsForToday(callback: (List<Habito>) -> Unit) {
        val userRef = userId?.let { database.child(it) } ?: return

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val todayCode = getDayCodeFromDate(currentDate)

        getDoneHabits(currentDate) { completedHabitIds ->
            userRef.child("habits").get()
                .addOnSuccessListener { snapshot ->
                    val pendingHabits = mutableListOf<Habito>()
                    for (habitSnapshot in snapshot.children) {
                        val habit = habitSnapshot.getValue(Habito::class.java)
                        habit?.let {
                            val frequency = it.frecuencia ?: emptyList()
                            val isForToday = todayCode in frequency
                            val isCompleted = completedHabitIds.contains(it.id)

                            if (isForToday && !isCompleted) {
                                pendingHabits.add(it)
                            }
                        }
                    }
                    callback(pendingHabits)
                }
                .addOnFailureListener {
                    callback(emptyList())
                }
        }
    }


    fun addColor(colorName: String, colorHex: String, callback: (Boolean) -> Unit) {
        val userRef = userId?.let { database.child(it) }
        userRef?.child("colors")?.child(colorName)?.setValue(colorHex)
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener {
                callback(false)
            }
    }

    fun getColors(callback: (List<Pair<String, String>>) -> Unit) {
        val userRef = userId?.let { database.child(it).child("colors") }
        userRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val colors = mutableListOf<Pair<String, String>>()
                for (colorSnapshot in snapshot.children) {
                    val colorName = colorSnapshot.key ?: ""
                    val colorHex = colorSnapshot.getValue(String::class.java) ?: ""
                    colors.add(Pair(colorName, colorHex))
                }
                callback(colors)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", "Error getting colors", error.toException())
            }
        })
    }

    fun addCategory(categoryName: String, callback: (Boolean) -> Unit) {
        val userRef = userId?.let { database.child(it) }
        userRef?.child("categories")?.child(categoryName)?.setValue(categoryName)
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener {
                callback(false)
            }
    }

    fun getCategories(callback: (List<String>) -> Unit) {
        val userRef = userId?.let { database.child(it).child("categories") }
        userRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<String>()
                for (categorySnapshot in snapshot.children) {
                    categorySnapshot.getValue(String::class.java)?.let {
                        categories.add(it)
                    }
                }
                callback(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", "Error getting categories", error.toException())
            }
        })
    }

    fun addHabit(habit: Habito, callback: (Boolean) -> Unit) {
        val userRef = userId?.let { database.child(it) }

        val habitId = userRef?.child("habits")?.push()?.key ?: return

        val habitWithId = habit.copy(id = habitId)

        userRef?.child("habits")?.child(habitId)?.setValue(habitWithId)
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener {
                callback(false)
            }
    }


    fun getHabits(callback: (List<Habito>) -> Unit) {
        val userRef = userId?.let { database.child(it).child("habits") }
        userRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val habitList = mutableListOf<Habito>()
                for (habitSnapshot in snapshot.children) {
                    val habit = habitSnapshot.getValue(Habito::class.java)
                    habit?.let { habitList.add(it) }
                }
                callback(habitList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", "Error getting habits", error.toException())
            }
        })
    }

    fun updateHabit(habit: Habito, callback: (Boolean) -> Unit) {
        val userRef = userId?.let { database.child(it) }
        userRef?.child("habits")?.child(habit.id)?.setValue(habit)
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener {
                callback(false)
            }
    }

    fun deleteHabit(habitId: String, callback: (Boolean) -> Unit) {
        val userRef = userId?.let { database.child(it) }
        userRef?.child("habits")?.child(habitId)?.removeValue()
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener {
                callback(false)
            }
    }

    fun updateCompletionStatus(habitId: String, isCompleted: Boolean, callback: (Boolean) -> Unit) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val userRef = userId?.let { database.child(it) }
        userRef?.child("daily_completions")?.child(currentDate)?.child(habitId)?.setValue(isCompleted)
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener {
                callback(false)
            }
    }


    fun getDoneHabits(date: String, callback: (List<String>) -> Unit) {
        val userRef = userId?.let { database.child(it) }
        userRef?.child("daily_completions")?.child(date)?.get()
            ?.addOnSuccessListener { snapshot ->
                val doneHabits = mutableListOf<String>()
                for (habitSnapshot in snapshot.children) {
                    if (habitSnapshot.getValue(Boolean::class.java) == true) {
                        doneHabits.add(habitSnapshot.key ?: "")
                    }
                }
                callback(doneHabits)
            }
            ?.addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getUndoneHabits(date: String, callback: (List<String>) -> Unit) {
        val userRef = userId?.let { database.child(it) }
        userRef?.child("daily_completions")?.child(date)?.get()
            ?.addOnSuccessListener { snapshot ->
                val undoneHabits = mutableListOf<String>()
                for (habitSnapshot in snapshot.children) {
                    if (habitSnapshot.getValue(Boolean::class.java) == false) {
                        undoneHabits.add(habitSnapshot.key ?: "")
                    }
                }
                callback(undoneHabits)
            }
            ?.addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getDayCodeFromDate(date: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateObj = sdf.parse(date) ?: return ""
        val calendar = Calendar.getInstance().apply { time = dateObj }

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "L"
            Calendar.TUESDAY -> "M"
            Calendar.WEDNESDAY -> "X"
            Calendar.THURSDAY -> "J"
            Calendar.FRIDAY -> "V"
            Calendar.SATURDAY -> "S"
            Calendar.SUNDAY -> "D"
            else -> ""
        }
    }
}



