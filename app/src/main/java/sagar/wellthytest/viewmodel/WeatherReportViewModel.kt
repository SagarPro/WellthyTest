package sagar.wellthytest.viewmodel

import androidx.lifecycle.ViewModel
import sagar.wellthytest.repository.WeatherReportRepository
import javax.inject.Inject

class WeatherReportViewModel

@Inject
internal constructor(repo: WeatherReportRepository): ViewModel(){

    private val repo: WeatherReportRepository = repo

}