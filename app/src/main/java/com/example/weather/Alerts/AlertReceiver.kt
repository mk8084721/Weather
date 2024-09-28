package com.example.weather.Alerts

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weather.R

class AlertReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        // Play the custom sound
        //playAlarmSound(context)

        // Show the notification
        showNotification(context, "Alarm", "It's time for your alert!")
    }

    private fun playAlarmSound(context: Context?) {
        // Define custom sound URI, you can use any custom sound URI
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        // Initialize MediaPlayer with the custom sound
        mediaPlayer = MediaPlayer.create(context, alarmSound)
        mediaPlayer?.start() // Start playing the alarm sound
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context?, title: String, message: String) {
        // Create an intent for the dismiss action
        val dismissIntent = Intent(context, DismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val builder = NotificationCompat.Builder(context!!, "alertChannel")
            .setSmallIcon(R.drawable.alert_svgrepo_com)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false) // Auto dismisses the notification when clicked
            .addAction(R.drawable.ic_wind, "Dismiss", dismissPendingIntent) // Add the dismiss button

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, builder.build())
    }

    fun stopAlarmSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /*@SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        // Show a toast message (for testing purposes)
        Toast.makeText(context, "Alert triggered!", Toast.LENGTH_SHORT).show()

        // Build the notification
        val builder = NotificationCompat.Builder(context, "alertChannel")
            .setSmallIcon(R.drawable.alert_svgrepo_com)
            .setContentTitle("Weather Alert")
            .setContentText("Your scheduled weather alert has been triggered.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())
    }*/

    /*@SuppressLint("MissingPermission")
    private fun showNotification(context: Context?, title: String, message: String) {
        val builder = NotificationCompat.Builder(context!!, "alertChannel")
            .setSmallIcon(R.drawable.alert_svgrepo_com)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, builder.build())
    }*/
    @SuppressLint("MissingPermission")
    private fun showOldNotification(context: Context?, title: String, message: String) {
        // Create an intent for the dismiss action
        val dismissIntent = Intent(context, DismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val alarmUri: Uri = Uri.parse("android.resource://" + (context?.packageName ?: "com.example.weather.Alerts") + "/" + R.raw.alarm_sound)
        // Build the notification
        val builder = NotificationCompat.Builder(context!!, "alertChannel")
            .setSmallIcon(R.drawable.alert_svgrepo_com)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmUri)
            .setAutoCancel(true) // Auto dismisses the notification when clicked
            .addAction(R.drawable.ic_wind, "Dismiss", dismissPendingIntent) // Add the dismiss button

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, builder.build())
    }
}