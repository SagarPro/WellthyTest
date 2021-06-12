package sagar.wellthytest.utils

import android.content.Context
import sagar.wellthytest.utils.AppConstants.PREF

class SharedPreferencesHelper(val context: Context) {

    fun putString(key: String, value: String) {
        val settings = context.getSharedPreferences(PREF, 0)
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        val settings = context.getSharedPreferences(PREF, 0)
        return settings.getString(key, "")
    }

}