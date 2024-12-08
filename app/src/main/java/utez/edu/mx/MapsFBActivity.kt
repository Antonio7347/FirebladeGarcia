package utez.edu.mx

import MarkerAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapsFBActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    private var userLocation: LatLng? = null
    private val markerItems = mutableListOf<MarkerItem>()
    private lateinit var markersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_fbactivity)

        setupFirebaseReference()
        setupMapFragment()
        setupButtons()
        setupRecyclerView()
    }

    private fun setupFirebaseReference() {
        val database = FirebaseDatabase.getInstance()
        markersRef = database.getReference("markers")
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnCenterLocation).setOnClickListener {
            userLocation?.let {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            } ?: Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = MarkerAdapter(markerItems)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        markersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                markerItems.clear()
                for (markerSnapshot in snapshot.children) {
                    val latitude = markerSnapshot.child("latitude").value as Double
                    val longitude = markerSnapshot.child("longitude").value as Double
                    val title = markerSnapshot.child("title").value as String
                    val markerPosition = LatLng(latitude, longitude)

                    markerItems.add(MarkerItem(title, "Lat: $latitude, Lng: $longitude"))

                    map.addMarker(
                        MarkerOptions()
                            .position(markerPosition)
                            .title("$title (Lat: $latitude, Lng: $longitude)")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsFBActivity, "Error al cargar marcadores: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        setupMapListeners()
        enableLocationIfPermitted()
    }

    private fun setupMapListeners() {
        map.setOnMapClickListener { latLng ->
            val markerTitle = "Punto de interés (Lat: ${latLng.latitude}, Lng: ${latLng.longitude})"
            val markerData = mapOf(
                "latitude" to latLng.latitude,
                "longitude" to latLng.longitude,
                "title" to markerTitle
            )
            markersRef.push().setValue(markerData)

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(markerTitle)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            markerItems.add(MarkerItem("Punto de interés", "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"))
        }
    }

    private fun enableLocationIfPermitted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }
        map.isMyLocationEnabled = true
        map.setOnMyLocationChangeListener { location: Location ->
            userLocation = LatLng(location.latitude, location.longitude)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationIfPermitted()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
