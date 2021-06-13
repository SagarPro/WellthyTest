package sagar.wellthytest.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import sagar.wellthytest.WellthyApp
import java.util.*

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    //initial setup for last updated language
    fun onLocaleCreate(context: Context): Context {
        var lang = "en"
        if (getPersistedData() != null){
            lang = getPersistedData()!!
        }
        return setLocale(context, lang)
    }

    //function which navigates to respective change language method by android version
    fun setLocale(context: Context, language: String?): Context {
        persist(language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language!!)
        }
        return updateResourcesLegacy(context, language!!)
    }

    //get last updated language
    private fun getPersistedData(): String? {
        val pref = SharedPreferencesHelper(WellthyApp.context)
        return pref.getString(SELECTED_LANGUAGE)
    }

    //store latest language
    private fun persist(language: String?) {
        val pref = SharedPreferencesHelper(WellthyApp.context)
        pref.putString(SELECTED_LANGUAGE, language!!)
    }

    //changes language if android version is N and greater
    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    //changes language if android version is less than N
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}