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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import utez.edu.mx.databinding.ActivityMaps2Binding


class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMaps2Binding
    private var mapTypeIndex = 0

    private val mapTypes = arrayOf(
        GoogleMap.MAP_TYPE_NORMAL,
        GoogleMap.MAP_TYPE_HYBRID,
        GoogleMap.MAP_TYPE_SATELLITE,
        GoogleMap.MAP_TYPE_TERRAIN
    )
    private fun changeMapType() {
        mapTypeIndex = (mapTypeIndex + 1) % mapTypes.size
        map.mapType = mapTypes[mapTypeIndex]
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        loadGlobalMarkers()
        loadUserReviews()
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
        loadUserReviews()
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
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para guardar una reseña", Toast.LENGTH_SHORT).show()
            return
        }
        val userUid = currentUser.uid
        val database = FirebaseDatabase.getInstance().reference.child("reviews").child(userUid)
        val reviewId = database.push().key ?: return
        val reviewData = mapOf(
            "title" to title,
            "latitude" to position.latitude,
            "longitude" to position.longitude,
            "review" to review
        )
        database.child(reviewId).setValue(reviewData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Reseña guardada exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar la reseña: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGlobalMarkers() {
        val database = FirebaseDatabase.getInstance().reference.child("markers")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (markerSnapshot in snapshot.children) {
                    val title = markerSnapshot.child("title").value as? String
                    val latitude = markerSnapshot.child("latitude").value as? Double
                    val longitude = markerSnapshot.child("longitude").value as? Double

                    if (title != null && latitude != null && longitude != null) {
                        val position = LatLng(latitude, longitude)
                        map.addMarker(
                            MarkerOptions().position(position).title(title)
                        )
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity2, "Error al cargar marcadores: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun loadUserReviews() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus reseñas", Toast.LENGTH_SHORT).show()
            return
        }
        val userUid = currentUser.uid
        val database = FirebaseDatabase.getInstance().reference.child("reviews").child(userUid)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (reviewSnapshot in snapshot.children) {
                    val title = reviewSnapshot.child("title").value as? String
                    val latitude = reviewSnapshot.child("latitude").value as? Double
                    val longitude = reviewSnapshot.child("longitude").value as? Double
                    val review = reviewSnapshot.child("review").value as? String

                    if (title != null && latitude != null && longitude != null && review != null) {
                        val position = LatLng(latitude, longitude)
                        map.addMarker(
                            MarkerOptions().position(position).title(title).snippet(review)
                        )
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity2, "Error al cargar reseñas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando ViewBinding
        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el fragmento del mapa y configurar el callback para cuando esté listo
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // 'this' indica que la clase actual implementa OnMapReadyCallback

        // Listener para cambiar el tipo de mapa (normal, satélite, híbrido, etc.)
        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener {
            changeMapType()
        }

        // Listener para mostrar las reseñas guardadas cuando se haga clic en el botón
        findViewById<Button>(R.id.btnViewReviews).setOnClickListener {
            displayReviewsDialog() // Método que muestra el diálogo con las reseñas
        }
    }




//    private fun displayReviewsDialog() {
//        val database = FirebaseDatabase.getInstance().reference.child("reviews")
//        database.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val reviews = mutableListOf<String>()
//                for (reviewSnapshot in snapshot.children) {
//                    val title = reviewSnapshot.child("title").getValue(String::class.java)
//                    val review = reviewSnapshot.child("review").getValue(String::class.java)
//                    val latitude = reviewSnapshot.child("latitude").getValue(Double::class.java)
//                    val longitude = reviewSnapshot.child("longitude").getValue(Double::class.java)
//
//                    if (title != null && review != null && latitude != null && longitude != null) {
//                        val location = "(${latitude}, ${longitude})"
//                        reviews.add("Lugar: $title\nUbicación: $location\nReseña: $review")
//                    }
//                }
//                if (reviews.isNotEmpty()) {
//                    showReviewsDialog(reviews)
//                } else {
//                    Toast.makeText(this@MapsActivity2, "No hay reseñas disponibles", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@MapsActivity2, "Error al cargar reseñas", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun displayReviewsDialog() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus reseñas", Toast.LENGTH_SHORT).show()
            return
        }

        val userUid = currentUser.uid
        val database = FirebaseDatabase.getInstance().reference.child("reviews").child(userUid)

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
                    Toast.makeText(this@MapsActivity2, "No tienes reseñas guardadas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity2, "Error al cargar reseñas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showReviewsDialog(reviews: List<String>) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Mis Reseñas")
            .setItems(reviews.toTypedArray(), null)
            .setPositiveButton("Cerrar", null)
            .create()

        dialog.show()
    }


}
