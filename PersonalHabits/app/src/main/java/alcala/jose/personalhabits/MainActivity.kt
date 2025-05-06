package alcala.jose.personalhabits

import alcala.jose.personalhabits.repositories.AuthRepository
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        authRepository = AuthRepository()

        val email: EditText = findViewById(R.id.etCorreo)
        val password: EditText = findViewById(R.id.etContraseña)
        val errorTv: TextView = findViewById(R.id.tvError)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonRegistrarse: Button = findViewById(R.id.btnRegistrarse)

        errorTv.visibility = View.INVISIBLE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonLogin.setOnClickListener {
            if (email.text.isEmpty() || password.text.isEmpty()) {
                showError("Todos los campos deben ser llenados", true)
            } else {
                login(email.text.toString(), password.text.toString())
            }
        }

        buttonRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }

    private fun goToMain(user: FirebaseUser) {
        val intent = Intent(this, MenuFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showError(text: String = "", visible: Boolean) {
        val errorTv: TextView = findViewById(R.id.tvError)
        errorTv.text = text
        errorTv.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            goToMain(currentUser)
        }
    }

    private fun login(email: String, password: String) {
        authRepository.login(email, password,
            onSuccess = { user ->
                showError(visible = false)
                goToMain(user)
            },
            onFailure = {
                showError("Usuario y/o contraseña equivocados", true)
            }
        )
    }
}
