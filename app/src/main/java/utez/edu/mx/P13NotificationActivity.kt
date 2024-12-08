package utez.edu.mx

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.channels.Channel

class P13NotificationActivity : AppCompatActivity() {
    private val channelId = "test_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p13_notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val channelName = "Test Channerl"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para notificaciones, prueba"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d("Depuracion", "Canal de notificaciones")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicitar el permiso si no está concedido
                requestNotificationPermission()
            } else {
                // Mostrar una notificación con retraso si el permiso ya está concedido
                mostrarNotificacionConRetraso()
            }
        } else {
            mostrarNotificacionConRetraso()
        }

        val btnNotificar = findViewById<Button>(R.id.btnNotificar)
        btnNotificar.setOnClickListener {
            try {
                mostrarNotificacionAvanzada()
                Log.d("Depuracion", "Notificación enviada correctamente")
            } catch (e: Exception) {
                Log.e("Error", "Error al enviar la notificación", e)
            }
        }

        val btnNotificationLarga = findViewById<Button>(R.id.btnNotificacionLarga)
        btnNotificationLarga.setOnClickListener {
            mostrarNotificacionConTextoLargo()
        }
    }

    private fun requestNotificationPermission() {
        val requestPermissionsLaucher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("Depuracion", "Permiso concedido")
                mostrarNotificacionConRetraso()
            } else {
                Log.d("Depuracion", "Permiso denegado")
            }
        }
        requestPermissionsLaucher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
        private fun mostrarNotificacionConRetraso() {
        window.decorView.postDelayed({
            try {
                mostrarNotificacion()
                Log.d("Depuracion", "Notificación enviada correctamente")
            } catch (e: Exception) {
                Log.e("Error", "Error al mostrar la notificación", e)
            }

        }, 2000)
    }
    private fun mostrarNotificacion() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono de la notificación
            .setContentTitle("Notificación de prueba") // Título de la notificación
            .setContentText("Esto es una notificación de prueba.") // Texto de la notificación
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Nivel de prioridad
            .setAutoCancel(true) // La notificación desaparece al tocarla
            .build()

        // Enviar la notificación
        NotificationManagerCompat.from(this).notify(1001, notification)
        Log.d("Depuración", "Notificación construida y enviada")
    }

    private fun mostrarNotificacionAvanzada() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_notification)

        // Validar si el recurso de la imagen no se carga correctamente
        if (bitmap == null) {
            Log.d("Depuración", "El recurso ic_notification no se cargó correctamente")
            return
        }

        val intent = Intent(this, P13NotificationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir la notificación avanzada
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_accept) // Ícono de la notificación
            .setContentTitle("Notificación Avanzada") // Título
            .setContentText("Texto inicial de la notificación.") // Texto principal
            .setStyle(
                NotificationCompat.BigPictureStyle() // Estilo con imagen grande
                    .bigPicture(bitmap) // Imagen grande
                    .bigLargeIcon(null as Bitmap?) // Sin ícono adicional
            )
            .setContentIntent(pendingIntent) // Acción al tocar
            .setAutoCancel(true) // La notificación desaparece al tocarla
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta
            .build()

        NotificationManagerCompat.from(this).notify(1003, notification)
        Log.d("Depuración", "Notificación avanzada enviada")
    }


    // Construye y envía una notificación con texto largo
    private fun mostrarNotificacionConTextoLargo() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_accept) // Ícono de la notificación
            .setContentTitle("Notificación con Texto Largo") // Título
            .setContentText("Texto inicial de la notificación.") // Texto corto visible sin expandir
            .setStyle(
                NotificationCompat.BigTextStyle() // Estilo con texto largo
                    .bigText(
                        "Este es un ejemplo de notificación con texto largo. "
                    )
            )
            .setAutoCancel(true) // Desaparece al tocarla
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta
            .build()


        // Enviar la notificación
        NotificationManagerCompat.from(this).notify(1004, notification)
        Log.d("Depuración", "Notificación con texto largo enviada")
    }



}
