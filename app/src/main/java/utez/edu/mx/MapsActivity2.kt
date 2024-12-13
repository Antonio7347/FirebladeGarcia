package utez.edu.mx

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import utez.edu.mx.databinding.ActivityMaps2Binding

class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var binding: ActivityMaps2Binding? = null
    private var mapTypeIndex = 0
    private val mapTypes = intArrayOf(
        GoogleMap.MAP_TYPE_NORMAL,
        GoogleMap.MAP_TYPE_HYBRID,
        GoogleMap.MAP_TYPE_SATELLITE,
        GoogleMap.MAP_TYPE_TERRAIN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setupUI()
    }

    private fun setupUI() {
        binding!!.btnChangeMapType.setOnClickListener { v -> changeMapType() }

        binding!!.btnOut.setOnClickListener { v ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isZoomControlsEnabled = true

        // Adding predefined markers

        // Allow user to set marker title
        map!!.setOnMapClickListener { latLng: LatLng ->
            showAddMarkerDialog(
                latLng
            )
        }
    }

    private fun adjustCameraToMarkers(locations: Array<LatLng>) {
        val boundsBuilder = LatLngBounds.Builder()
        for (location in locations) {
            boundsBuilder.include(location)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150))
    }

    private fun showAddMarkerDialog(latLng: LatLng) {
        val input = EditText(this)
        input.hint = "Escribe un título para el marcador"

        AlertDialog.Builder(this)
            .setTitle("Agregar marcador")
            .setView(input)
            .setPositiveButton("Guardar") { dialog: DialogInterface?, which: Int ->
                val title = input.text.toString().trim { it <= ' ' }
                if (!title.isEmpty()) {
                    map!!.addMarker(MarkerOptions().position(latLng).title(title))
                } else {
                    Toast.makeText(
                        this,
                        "El título no puede estar vacío",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun changeMapType() {
        mapTypeIndex = (mapTypeIndex + 1) % mapTypes.size
        map!!.mapType = mapTypes[mapTypeIndex]
    }
}