package sagar.wellthytest.repository

import sagar.wellthytest.api.ApiInterface
import javax.inject.Singleton

@Singleton
class WeatherReportRepository(api: ApiInterface) {

    private val api: ApiInterface = api


}