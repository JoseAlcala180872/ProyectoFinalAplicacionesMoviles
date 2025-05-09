package alcala.jose.personalhabits.Repositories

import alcala.jose.personalhabits.Charts.ChartDTO
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class UserRepository {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val categoryRepository = CategoryRepository()


    fun get_uid(): String {
        return uid
    }

    fun getCategories(): List<String> {
        return categoryRepository.getCategories()
    }



}
