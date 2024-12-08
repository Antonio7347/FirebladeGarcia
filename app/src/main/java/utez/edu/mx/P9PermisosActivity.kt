package utez.edu.mx

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class P9PermisosActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvCoordenadas: TextView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_p9_permisos)

        val btnObtenerUbicacion: Button = findViewById(R.id.btn_obtener_ubicacion)
        tvCoordenadas = findViewById(R.id.tv_coordenadas)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnObtenerUbicacion.setOnClickListener {
            obtenerUbicacion()
        }
    }

    // Función para obtener la ubicación del dispositivo
    private fun obtenerUbicacion() {
        // Verificar si los permisos de ubicación han sido concedidos
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no se han concedido, solicita los permisos de ubicación
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Si los permisos ya están concedidos, intenta obtener la última ubicación conocida
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Si se obtiene la ubicación, extrae latitud y longitud
                        val latitud = location.latitude
                        val longitud = location.longitude
                        // Muestra las coordenadas en el TextView
                        tvCoordenadas.text = "Coordenadas: $latitud, $longitud"
                    } else {
                        // Manejo en caso de que no se obtenga la ubicación
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
