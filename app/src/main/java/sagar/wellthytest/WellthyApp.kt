package sagar.wellthytest

import android.annotation.SuppressLint
import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import sagar.wellthytest.di.component.AppComponent
import sagar.wellthytest.di.component.DaggerAppComponent
import sagar.wellthytest.utils.SharedPreferencesHelper

class WellthyApp: DaggerApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var component: AppComponent? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //SharedPreferencesHelper(context)// initialize sharedPreferences
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        component = DaggerAppComponent
            .builder()
            .application(this@WellthyApp)
            .build()
        return component
    }

}