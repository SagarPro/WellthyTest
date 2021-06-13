package sagar.wellthytest.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import sagar.wellthytest.ui.WeatherReportActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeWeatherReportActivity(): WeatherReportActivity

}