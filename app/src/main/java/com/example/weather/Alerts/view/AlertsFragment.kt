package com.example.weather.Alerts.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.database.model.Alert
import com.example.weather.databinding.FragmentAlertsBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.repeatOnLifecycle
import com.example.weather.Alerts.AlertReceiver
import com.example.weather.Alerts.viewModel.AlertViewModelFactory
import com.example.weather.Alerts.viewModel.AlertsViewModel
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.LocalDataSource
import com.example.weather.network.API
import com.example.weather.network.ApiState
import kotlinx.coroutines.flow.collectLatest


class AlertsFragment : Fragment(), ICommunicate{
    lateinit var binding: FragmentAlertsBinding
    lateinit var viewModel : AlertsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alertsFactory = AlertViewModelFactory(WeatherRepo(LocalDataSource(requireContext()), API.retrofitService))
        viewModel = ViewModelProvider(this , alertsFactory).get(AlertsViewModel::class.java)
        viewModel.getAlerts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        binding.fab.setOnClickListener {
            val currentDate = Calendar.getInstance()

            // Show DatePickerDialog
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // Show TimePickerDialog after selecting date
                    TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->
                            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            selectedDate.set(Calendar.MINUTE, minute)

                            // Add alert to database
                            addAlertToDatabase(selectedDate.timeInMillis)

                        },
                        currentDate.get(Calendar.HOUR_OF_DAY),
                        currentDate.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNotificationChannel(requireContext())
        val alertsAdapter = AlertsAdapter(this)
        binding.alertsRecyclerView.apply {
            adapter = alertsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.alertsSF.collectLatest{
                        result ->
                    when(result){
                        is ApiState.Loading ->{
                            //
                        }
                        is ApiState.AlertSuccess ->{
                            var alerts = result.data
                            alertsAdapter.submitList(alerts)
                        }
                        is ApiState.Failure->{
                            Toast.makeText(
                                requireContext(),
                                "Check Local",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alert Channel"
            val descriptionText = "Channel for alert notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alertChannel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun addAlertToDatabase(timeInMillis: Long) {

        val alert = Alert(title = "Alert", timeInMillis = timeInMillis)

        // Insert alert into Room (use coroutines or background thread)
        lifecycleScope.launch {
            viewModel.insertAlertToDB(alert)
            // Schedule the alarm
//            scheduleAlert(alert)
        }
        scheduleAlert(alert)
    }


    private fun scheduleAlert(alert: Alert) {
        /*
        //val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        //val pendingIntent = PendingIntent.getBroadcast(requireContext(), alert.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Direct the user to the settings page to grant the permission
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), alert.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alert.timeInMillis, pendingIntent)
        }*/
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if the app can schedule exact alarms on Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(requireContext(), AlertReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(requireContext(), alert.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alert.timeInMillis, pendingIntent)
            } else {
                // Request permission to schedule exact alarms
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        } else {
            val intent = Intent(requireContext(), AlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), alert.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alert.timeInMillis, pendingIntent)
        }

    }
    override fun deleteAlert(alert: Alert) {
        removeAlert(alert)
    }

    private fun removeAlert(alert: Alert) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create the same Intent and PendingIntent used to schedule the alert
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alert.id, // Use the same unique ID for this alert
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the scheduled alarm
        alarmManager.cancel(pendingIntent)

        // Remove the alert from the database
        lifecycleScope.launch {
            viewModel.deleteAlertFromDB(alert)
            Toast.makeText(requireContext(), "Alert removed", Toast.LENGTH_SHORT).show()
        }
    }




}