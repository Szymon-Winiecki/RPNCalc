package com.example.rpncalc

import android.content.Context
import android.content.SharedPreferences

class Settings {
    private val SETTINGS_PREFERENCES_NAME = "SETTINGS_PREFERENCES"
    private val DECIMAL_PLCACES_STRING = "DECIMAL_PLACES"
    private val DARK_THEME_STRING = "DARK_THEME"

    private var settingsSharedPreferences : SharedPreferences

    var decimalPlaces : Int = 1
        get() {
            field = settingsSharedPreferences.getInt(DECIMAL_PLCACES_STRING, field)
            return field
        }
        set(value) {
            field = value
            with (settingsSharedPreferences.edit()) {
                putInt(DECIMAL_PLCACES_STRING, value)
                apply()
            }
        }

    var darkTheme : Boolean = false
        get() {
            field = settingsSharedPreferences.getBoolean(DARK_THEME_STRING, field)
            return field
        }
        set(value){
            field = value
            with(settingsSharedPreferences.edit()){
                putBoolean(DARK_THEME_STRING, value)
                apply()
            }
        }

    constructor(context: Context){
        settingsSharedPreferences = context.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
}