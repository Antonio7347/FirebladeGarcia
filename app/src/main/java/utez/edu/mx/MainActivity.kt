package utez.edu.mx

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import utez.edu.mx.adapter.RestauranteAdapter
import utez.edu.mx.models.Restaurante

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestauranteAdapter
    private val listaRestaurantes = mutableListOf<Restaurante>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        recyclerView = findViewById(R.id.recyclerViewRestaurantes)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = RestauranteAdapter(listaRestaurantes)
//        recyclerView.adapter = adapter
//
//        database = FirebaseDatabase.getInstance().getReference("Restaurantes")
//        cargarDatos()
//    }
private fun agregarRestaurante() {
    // Crea un nuevo restaurante
    val nuevoRestaurante = Restaurante(
        "Restaurante de Prueba",
        "Este es un restaurante de prueba",
        "https://example.com/restaurante.jpg"
    )

    // Envía los datos a Firebase
    database.push().setValue(nuevoRestaurante).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(this, "Restaurante agregado exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al agregar restaurante", Toast.LENGTH_SHORT).show()
        }
    }
}


    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Configuración del RecyclerView
    recyclerView = findViewById(R.id.recyclerViewRestaurantes)
    recyclerView.layoutManager = LinearLayoutManager(this)
    adapter = RestauranteAdapter(listaRestaurantes)
    recyclerView.adapter = adapter

    // Referencia a la base de datos
    database = FirebaseDatabase.getInstance().getReference("Restaurantes")

    // Cargar datos desde Firebase
    cargarDatos()

    // Botón para agregar un restaurante
    val btnAgregarRestaurante: Button = findViewById(R.id.btnAgregarRestaurante)
    btnAgregarRestaurante.setOnClickListener {
        agregarRestaurante() // Llama a la función al hacer clic
    }
}

    private fun cargarDatos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaRestaurantes.clear()
                for (data in snapshot.children) {
                    val restaurante = data.getValue(Restaurante::class.java)
                    restaurante?.let { listaRestaurantes.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
