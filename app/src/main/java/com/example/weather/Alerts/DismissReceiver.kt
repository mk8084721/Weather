package com.example.weather.Alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationManagerCompat

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Cancel the notification when the dismiss button is pressed
        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.cancel(1) // Cancel the notification with the given ID

        // Optionally stop any playing sound
        val ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }
}