package utez.edu.mx

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import utez.edu.mx.adapter.UserAdapter
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import utez.edu.mx.models.User
import com.android.volley.toolbox.Volley
import utez.edu.mx.R

class P10VolleyActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_p10_volley)

        // Inicializar el RecyclerView y asignar un LayoutManager
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Llamada al método para obtener los datos de los usuarios
        fetchUsers()
    }

    // Método para obtener los datos de los usuarios
    private fun fetchUsers() {
        val url = "https://jsonplaceholder.typicode.com/users"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                val users = ArrayList<User>()
                for (i in 0 until response.length()) {
                    val userJson = response.getJSONObject(i)
                    val name = userJson.getString("name")
                    val email = userJson.getString("email")
                    val user = User(name, email)
                    users.add(user)
                }
                userAdapter = UserAdapter(users)
                recyclerView.adapter = userAdapter
            },
            { error ->
                Log.e("Volley", "Error: ${error.message}")
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

}
