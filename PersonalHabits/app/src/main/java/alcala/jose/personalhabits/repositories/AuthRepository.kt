package alcala.jose.personalhabits.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun login(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        onSuccess(user)
                    } ?: onFailure(null)
                } else {
                    onFailure(task.exception)
                }
            }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val userData = mapOf("name" to name)
                        database.child("users").child(userId).setValue(userData)
                            .addOnSuccessListener {
                                Log.d("FirebaseDB", "User data saved!")
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirebaseDB", "Error saving user data", e)
                                onFailure(e)
                            }
                    } else {
                        onFailure(null)
                    }
                } else {
                    Log.e("Auth", "Registration failed", task.exception)
                    onFailure(task.exception)
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logout() {
        auth.signOut()
    }
}
