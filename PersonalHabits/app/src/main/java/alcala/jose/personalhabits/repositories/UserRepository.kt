package alcala.jose.personalhabits.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class UserRepository {
    private val database = FirebaseDatabase.getInstance().reference
    internal val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun getCategories(callback: (List<String>) -> Unit) {
        val userRef = userId?.let { database.child("users").child(it).child("categories") }
        userRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                callback(categories)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addCategory(category: String, callback: (Boolean) -> Unit) {
        userId?.let {
            database.child("users").child(it).child("categories").child(category).setValue(category)
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        }
    }

    fun updateCompletionStatus(habitId: String, isCompleted: Boolean, callback: (Boolean) -> Unit) {
        val date = dateFormat.format(Date())
        val userCompletionRef = database.child("users").child(userId.toString()).child("daily_completions").child(date)

        userCompletionRef.child(habitId).setValue(isCompleted)
            .addOnSuccessListener {
                checkAndUpdateStreak { streak ->
                    Log.d("UserRepository", "Streak updated to $streak")
                    callback(true)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getDoneHabits(date: String, callback: (List<String>) -> Unit) {
        database.child("users").child(userId.toString()).child("daily_completions").child(date).get()
            .addOnSuccessListener { snapshot ->
                val doneHabits = snapshot.children.filter {
                    it.getValue(Boolean::class.java) == true
                }.mapNotNull { it.key }
                callback(doneHabits)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getDayCodeFromDate(date: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateObj = sdf.parse(date) ?: return ""
        return when (Calendar.getInstance().apply { time = dateObj }.get(Calendar.DAY_OF_WEEK)) {
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

    fun getStreak(callback: (Int) -> Unit) {
        database.child("users").child(userId.toString()).child("streak").get()
            .addOnSuccessListener { snapshot ->
                val streak = snapshot.getValue(Int::class.java) ?: 0
                callback(streak)
            }
            .addOnFailureListener {
                callback(0)
            }
    }


    private fun checkAndUpdateStreak(callback: (Int) -> Unit) {
        val today = dateFormat.format(Date())
        val yesterday = Calendar.getInstance().apply {
            time = dateFormat.parse(today)!!
            add(Calendar.DATE, -1)
        }.let { dateFormat.format(it.time) }

        val userRef = database.child("users").child(userId.toString())

        userRef.child("daily_completions").child(today)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(todaySnapshot: DataSnapshot) {
                    val hasCompletedToday = todaySnapshot.children.any {
                        it.getValue(Boolean::class.java) == true
                    }

                    if (!hasCompletedToday) {
                        callback(0)
                        return
                    }

                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val lastDate = snapshot.child("lastCompletionDate").getValue(String::class.java)
                            val currentStreak = snapshot.child("streak").getValue(Int::class.java) ?: 0

                            val newStreak = when {
                                lastDate == yesterday -> currentStreak + 1
                                lastDate == today -> currentStreak
                                else -> 1
                            }

                            userRef.child("streak").setValue(newStreak)
                            userRef.child("lastCompletionDate").setValue(today)
                            callback(newStreak)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            callback(0)
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(0)
                }
            })
    }
}
