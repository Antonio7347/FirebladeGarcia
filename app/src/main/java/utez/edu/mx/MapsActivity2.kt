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
import com.google.android.gms.maps.model.Marker
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
        binding!!.btnChangeMapType.setOnClickListener { changeMapType() }

        binding!!.btnOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isZoomControlsEnabled = true

        // Agregar un marcador con título personalizado
        map!!.setOnMapClickListener { latLng ->
            showAddMarkerDialog(latLng)
        }

        // Manejar clics en los marcadores
        map!!.setOnMarkerClickListener { marker ->
            showEditOrDeleteDialog(marker)
            true // Devuelve true para indicar que el evento ha sido manejado
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
            .setPositiveButton("Guardar") { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    map!!.addMarker(MarkerOptions().position(latLng).title(title))
                } else {
                    Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditOrDeleteDialog(marker: Marker) {
        AlertDialog.Builder(this)
            .setTitle("Editar o eliminar marcador")
            .setMessage("¿Qué deseas hacer con el marcador seleccionado?")
            .setPositiveButton("Editar") { _, _ ->
                showEditMarkerDialog(marker)
            }
            .setNegativeButton("Eliminar") { _, _ ->
                marker.remove()
                Toast.makeText(this, "Marcador eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    private fun showEditMarkerDialog(marker: Marker) {
        val input = EditText(this)
        input.hint = "Escribe un nuevo título"
        input.setText(marker.title)

        AlertDialog.Builder(this)
            .setTitle("Editar marcador")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val newTitle = input.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    marker.title = newTitle
                    marker.showInfoWindow()
                    Toast.makeText(this, "Marcador actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
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
