package alcala.jose.personalhabits

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        auth = Firebase.auth

        val name: EditText =findViewById(R.id.etrNombre)
        val email: EditText =findViewById(R.id.etrCorreo)
        val password: EditText =findViewById(R.id.etrContraseña)
        val confirmPassword: EditText =findViewById(R.id.etrRepetirContraseña)
        val button: Button =findViewById(R.id.btnRegistrarse)
        val errorTv: TextView=findViewById(R.id.trvError)

        button.setOnClickListener({
            if(name.text.isEmpty()||email.text.isEmpty()||password.text.isEmpty()||confirmPassword.text.isEmpty()){
                errorTv.text="Todos los campos deben ser llenados"
                errorTv.visibility= View.VISIBLE
            } else if (!(password.text.toString().equals(confirmPassword.text.toString()))){
                errorTv.text="Las contraseñas no coinciden"
                errorTv.visibility= View.VISIBLE
            } else {
                errorTv.visibility= View.INVISIBLE
                registrar(email.text.toString(), password.text.toString())
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun registrar(email:String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                    task-> if (task.isSuccessful){
                Log.d("INFO", "signInWithEmail:Success" )
                val user = auth.currentUser
                val intenr = Intent(this, MainActivity:: class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                Log.d("ERROR", "signInWithEmail:Failure", task.exception )
                Toast.makeText(
                    baseContext,
                    "El registro falló",
                    Toast.LENGTH_SHORT
                ).show()
            }
            }
    }


}