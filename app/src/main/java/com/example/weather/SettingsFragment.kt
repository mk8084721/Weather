package com.example.weather

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.compose.material3.Switch
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var languageSwitch: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageSwitch = view.findViewById(R.id.language_switch)

        // Load the current language setting
        val sharedPreferences = requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val isArabic = sharedPreferences.getString("lang", "en")
        languageSwitch.isChecked = (isArabic=="ar")

        languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changeLanguage("ar")
                languageSwitch.text = "English" // Change switch text to English
            } else {
                changeLanguage("en")
                languageSwitch.text = "العربية" // Change switch text to Arabic
            }
        }
    }

    private fun changeLanguage(languageCode: String) {
        val config = resources.configuration
        config.setLocale(Locale(languageCode))
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the selected language in SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("lang",languageCode).apply()
        // Restart the activity to apply changes
        requireActivity().recreate()
    }
}