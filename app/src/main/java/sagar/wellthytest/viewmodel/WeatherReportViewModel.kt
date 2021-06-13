package sagar.wellthytest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import sagar.wellthytest.api.ApiResponse
import sagar.wellthytest.model.WeatherReportModel
import sagar.wellthytest.repository.WeatherReportRepository
import javax.inject.Inject

class WeatherReportViewModel

@Inject
internal constructor(repo: WeatherReportRepository): ViewModel(){

    private val repo: WeatherReportRepository = repo

    //gets city name from view and navigates to repository to call api
    fun getWeatherReport(city: String): LiveData<ApiResponse<WeatherReportModel>> {
        return repo.getWeatherReport(city)
    }

}