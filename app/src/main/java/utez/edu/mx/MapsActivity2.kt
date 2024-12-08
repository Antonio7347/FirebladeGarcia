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
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el fragmento del mapa y notificar cuando esté listo.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar botón para cambiar el tipo de mapa
        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener {
            changeMapType()
        }
    }

    private fun changeMapType() {
        mapTypeIndex = (mapTypeIndex + 1) % mapTypes.size // Cambiar al siguiente tipo de mapa
        map.mapType = mapTypes[mapTypeIndex] // Aplicar el tipo de mapa
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Habilitar controles de zoom
        map.uiSettings.isZoomControlsEnabled = true
        map.isBuildingsEnabled = true
        map.isIndoorEnabled = true
        map.isTrafficEnabled = true

        // Configuración inicial
        val mexicoCity = LatLng(19.432608, -99.133209)
        val guadalajara = LatLng(20.659698, -103.349609)
        val miCasa = LatLng(18.729407, -99.163642)

        // Agregar marcadores
        map.addMarker(MarkerOptions().position(mexicoCity).title("Marker in Mexico City"))
        map.addMarker(MarkerOptions().position(guadalajara).title("Marker in Guadalajara").snippet("Capital de Jalisco"))
        map.addMarker(MarkerOptions().position(miCasa).title("Mi Casa").snippet("Casa de Antonio"))

        // Ajustar la cámara para mostrar ambos marcadores
        val bounds = LatLngBounds.Builder()
            .include(mexicoCity)
            .include(guadalajara)
            .build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150)) // Padding para espacio
    }
}
