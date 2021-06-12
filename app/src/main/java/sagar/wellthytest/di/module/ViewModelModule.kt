package sagar.wellthytest.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import sagar.wellthytest.di.factory.ViewModelFactory
import sagar.wellthytest.di.interfaces.ViewModelKey
import sagar.wellthytest.viewmodel.WeatherReportViewModel

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WeatherReportViewModel::class)
    abstract fun bindWeatherReportViewModel(weatherReportViewModel: WeatherReportViewModel): ViewModel

}