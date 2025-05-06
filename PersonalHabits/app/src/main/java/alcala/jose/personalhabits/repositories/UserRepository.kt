package alcala.jose.personalhabits.repositories

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
    private val database = FirebaseDatabase.getInstance().reference
    internal val userId = FirebaseAuth.getInstance().currentUser?.uid

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
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        // Use push() to create a new unique ID for each entry, instead of overwriting
        val userCompletionRef = database.child("users").child(userId.toString()).child("daily_completions").child(date)

        userCompletionRef.child(habitId).setValue(isCompleted)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }


    fun getDoneHabits(date: String, callback: (List<String>) -> Unit) {
        database.child("users").child(userId.toString()).child("daily_completions").child(date).get()
            .addOnSuccessListener { snapshot ->
                val doneHabits = snapshot.children.filter {
                    it.getValue(Boolean::class.java) == true
                }.mapNotNull { it.key }
                callback(doneHabits)
            }
            .addOnFailureListener { callback(emptyList()) }
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
}
