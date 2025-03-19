package alcala.jose.personalhabits

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        val email: EditText =findViewById(R.id.etCorreo)
        val password: EditText =findViewById(R.id.etContraseña)
        val errorTv: TextView =findViewById(R.id.tvError)
        val buttonLogin: Button =findViewById(R.id.buttonLogin)
        val buttonRegistrarse: Button =findViewById(R.id.btnRegistrarse)

        errorTv.visibility= View.INVISIBLE


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonLogin.setOnClickListener {
            if(email.text.isEmpty()||password.text.isEmpty()){
                showError("Todos los campos deben ser llenados",true)
            } else {
                login(email.text.toString(), password.text.toString())
            }

        }
        buttonRegistrarse.setOnClickListener{
            var intent: Intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }

    fun goToMain (user:FirebaseUser){
        var intent: Intent = Intent(this, Habito::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun showError(text:String="",visible:Boolean){
        val errorTv: TextView = findViewById(R.id.tvError)
        errorTv.text=text
        errorTv.visibility=if(visible)View.VISIBLE else View.INVISIBLE
    }

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null){
            goToMain(currentUser)
        }
    }

    fun login(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    showError(visible = false)
                    goToMain(user!!)

                } else {
                    showError("Usuario y/o contraseña equivocados", true)
                }
            }
    }
    }