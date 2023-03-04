package com.example.rpncalc

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.rpncalc.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ustawienia"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed();
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var settings : Settings


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            settings = Settings(requireContext())

            val decimalsPreference: EditTextPreference? = findPreference("displayPrecision")
            decimalsPreference?.setOnBindEditTextListener { preference ->
                preference.inputType = InputType.TYPE_CLASS_NUMBER
            }

            decimalsPreference?.setOnPreferenceChangeListener(){ _, value ->
                Log.d("SETTINGS", "Changed decimals to ${value}")
                var decimals : Int = 0
                try {
                    decimals = value.toString().toInt()
                } catch(e : NumberFormatException) {
                    //TODO
                }

                settings.decimalPlaces = decimals
                true
            }

            val darkThemePreference: SwitchPreferenceCompat? = findPreference("darkTheme")

            darkThemePreference?.setOnPreferenceChangeListener(){ _, value ->
                settings.darkTheme = value.toString().toBooleanStrict()

                if(settings.darkTheme){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                true
            }
        }
    }
}