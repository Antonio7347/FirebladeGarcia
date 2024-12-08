package utez.edu.mx


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


// Servicio personalizado para gestionar los mensajes recibidos desde Firebase Cloud Messaging (FCM)
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {


    // Método que se llama automáticamente cuando se recibe un mensaje desde FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        // Log para depurar y mostrar el origen del mensaje recibido
        Log.d("FCM", "Mensaje recibido de: ${remoteMessage.from}")


        // Verificar si el mensaje contiene una notificación
        remoteMessage.notification?.let {
            // Log para mostrar los detalles de la notificación (título y cuerpo)
            Log.d("FCM", "Título: ${it.title}")
            Log.d("FCM", "Cuerpo: ${it.body}")


            // Llamar al método para mostrar la notificación al usuario
            mostrarNotificacion(it.title ?: "Notificación", it.body ?: "Contenido vacío")
        }
    }


    // Método para crear y mostrar una notificación
    private fun mostrarNotificacion(title: String, body: String) {
        val channelId = "fcm_channel" // Identificador único para el canal de notificación


        // Crear el canal de notificación (requerido en Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, // Identificador del canal
                "Canal FCM", // Nombre del canal visible para el usuario
                NotificationManager.IMPORTANCE_HIGH // Importancia alta para mostrar banners
            ).apply {
                description = "Canal para notificaciones de Firebase" // Descripción del canal
            }
            // Registrar el canal en el sistema
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }


        // Construcción de la notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícono de la notificación
            .setContentTitle(title) // Título de la notificación
            .setContentText(body) // Cuerpo de la notificación
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridad para alertas visibles
            .setAutoCancel(true) // La notificación desaparece al tocarla
            .build()


        // Mostrar la notificación al usuario
        NotificationManagerCompat.from(this).notify(1002, notification)
    }
}
