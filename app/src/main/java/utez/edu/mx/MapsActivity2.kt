package utez.edu.mx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import utez.edu.mx.databinding.ActivityMaps2Binding


class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMaps2Binding
    private var mapTypeIndex = 0 // Índice para alternar entre los tipos de mapa

    private val mapTypes = arrayOf(
        GoogleMap.MAP_TYPE_NORMAL,
        GoogleMap.MAP_TYPE_HYBRID,
        GoogleMap.MAP_TYPE_SATELLITE,
        GoogleMap.MAP_TYPE_TERRAIN
    )

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMaps2Binding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Obtener el fragmento del mapa y notificar cuando esté listo.
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        // Configurar botón para cambiar el tipo de mapa
//        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener {
//            changeMapType()
//        }
//    }
//
    private fun changeMapType() {
        mapTypeIndex = (mapTypeIndex + 1) % mapTypes.size // Cambiar al siguiente tipo de mapa
        map.mapType = mapTypes[mapTypeIndex] // Aplicar el tipo de mapa
    }

//
//    override fun onMapReady(googleMap: GoogleMap) {
//        map = googleMap
//        // Habilitar controles de zoom
//        map.uiSettings.isZoomControlsEnabled = true
//        map.isBuildingsEnabled = true
//        map.isIndoorEnabled = true
//        map.isTrafficEnabled = true
//
//        // Configuración inicial
//        val mexicoCity = LatLng(19.432608, -99.133209)
//        val guadalajara = LatLng(20.659698, -103.349609)
//        val miCasa = LatLng(18.729407, -99.163642)
//
//        // Agregar marcadores
//        map.addMarker(MarkerOptions().position(mexicoCity).title("Neko cafe"))
//        map.addMarker(MarkerOptions().position(guadalajara).title("Cafe italiano").snippet("Capital de Jalisco"))
//        map.addMarker(MarkerOptions().position(miCasa).title("Cafe Mexicano").snippet("Casa de Antonio"))
//
//        // Ajustar la cámara para mostrar ambos marcadores
//        val bounds = LatLngBounds.Builder()
//            .include(mexicoCity)
//            .include(guadalajara)
//            .build()
//        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150)) // Padding para espacio
//    }


override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    map.uiSettings.isZoomControlsEnabled = true

    val locations = listOf(
        Pair(LatLng(19.432608, -99.133209), "Neko cafe"),
        Pair(LatLng(20.659698, -103.349609), "Cafe italiano"),
        Pair(LatLng(18.729407, -99.163642), "Cafe Mexicano")
    )

    for ((latLng, title) in locations) {
        map.addMarker(
            MarkerOptions().position(latLng).title(title).snippet("Haz clic para agregar una reseña")
        )
    }

    map.setOnInfoWindowClickListener { marker ->
        showReviewDialog(marker.title, marker.position)
    }

    val bounds = LatLngBounds.Builder()
    locations.forEach { bounds.include(it.first) }
    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150))
}

    private fun showReviewDialog(title: String?, position: LatLng) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_review, null)
        val reviewEditText = dialogView.findViewById<EditText>(R.id.reviewEditText)

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Reseña para $title")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogView.findViewById<Button>(R.id.submitReviewButton).setOnClickListener {
            val review = reviewEditText.text.toString()
            if (review.isNotEmpty()) {
                saveReviewToFirebase(title, position, review)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor escribe una reseña", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun saveReviewToFirebase(title: String?, position: LatLng, review: String) {
        val database = FirebaseDatabase.getInstance().reference.child("reviews")
        val reviewId = database.push().key ?: return

        val reviewData = mapOf(
            "title" to title,
            "latitude" to position.latitude,
            "longitude" to position.longitude,
            "review" to review
        )

        database.child(reviewId).setValue(reviewData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Reseña guardada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar reseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadReviews() {
        val database = FirebaseDatabase.getInstance().reference.child("reviews")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (reviewSnapshot in snapshot.children) {
                    val title = reviewSnapshot.child("title").getValue(String::class.java)
                    val latitude = reviewSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = reviewSnapshot.child("longitude").getValue(Double::class.java)
                    val review = reviewSnapshot.child("review").getValue(String::class.java)

                    if (title != null && latitude != null && longitude != null && review != null) {
                        map.addMarker(
                            MarkerOptions()
                                .position(LatLng(latitude, longitude))
                                .title(title)
                                .snippet(review)
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity2, "Error al cargar reseñas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener {
            changeMapType()
        }

        findViewById<Button>(R.id.btnViewReviews).setOnClickListener {
            displayReviewsDialog()
        }
    }

    private fun displayReviewsDialog() {
        val database = FirebaseDatabase.getInstance().reference.child("reviews")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = mutableListOf<String>()
                for (reviewSnapshot in snapshot.children) {
                    val title = reviewSnapshot.child("title").getValue(String::class.java)
                    val review = reviewSnapshot.child("review").getValue(String::class.java)
                    val latitude = reviewSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = reviewSnapshot.child("longitude").getValue(Double::class.java)

                    if (title != null && review != null && latitude != null && longitude != null) {
                        val location = "(${latitude}, ${longitude})"
                        reviews.add("Lugar: $title\nUbicación: $location\nReseña: $review")
                    }
                }

                if (reviews.isNotEmpty()) {
                    showReviewsDialog(reviews)
                } else {
                    Toast.makeText(this@MapsActivity2, "No hay reseñas disponibles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity2, "Error al cargar reseñas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showReviewsDialog(reviews: List<String>) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Reseñas Guardadas")
            .setItems(reviews.toTypedArray(), null)
            .setPositiveButton("Cerrar", null)
            .create()

        dialog.show()
    }




}
