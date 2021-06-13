package sagar.wellthytest

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.android.synthetic.main.item_theme.*
import sagar.wellthytest.di.component.AppComponent
import sagar.wellthytest.di.component.DaggerAppComponent
import sagar.wellthytest.utils.AppConstants
import sagar.wellthytest.utils.SharedPreferencesHelper

class WellthyApp: DaggerApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var component: AppComponent? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var pref: SharedPreferencesHelper
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        pref = SharedPreferencesHelper(context)
        assignTheme()
    }

    private fun assignTheme(){
        var currentTheme = pref.getString(AppConstants.SELECTED_THEME)
        if (currentTheme == null){
            currentTheme = AppConstants.THEME_LIGHT
        }
        //set switch to true or false based on the theme value
        if (currentTheme == AppConstants.THEME_LIGHT){
            changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun changeTheme(mode: Int){
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        component = DaggerAppComponent
            .builder()
            .application(this@WellthyApp)
            .build()
        return component
    }

}