package com.example.weather

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AlarmAlertActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_alert)

        // Play alarm sound
        playAlarmSound()

        // Dismiss button to stop the alarm and sound
        val dismissButton: MaterialButton = findViewById(R.id.dismissButton)
        dismissButton.setOnClickListener {
            // Stop the sound
            stopAlarmSound()

            // Finish the activity (alarm dismissed)
            finish()
        }
    }

    private fun playAlarmSound() {
        // Use the default alarm sound
        val alarmUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm_sound)
        mediaPlayer = MediaPlayer.create(this, alarmUri)
        mediaPlayer.isLooping = true  // Set the sound to loop until dismissed
        mediaPlayer.start()  // Play the sound
    }

    private fun stopAlarmSound() {
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()  // Release resources after stopping the sound
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound()  // Ensure the sound is stopped when the activity is destroyed
    }
}
