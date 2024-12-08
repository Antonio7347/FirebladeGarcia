package utez.edu.mx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "El correo electrónico es obligatorio"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "La contraseña es obligatoria"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordEditText.error = "La contraseña debe tener al menos 6 caracteres"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "El correo electrónico es obligatorio"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "La contraseña es obligatoria"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, P8RealtimeDatabaseActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}