package sagar.wellthytest.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sagar.wellthytest.api.ApiInterface
import sagar.wellthytest.api.ApiResponse
import sagar.wellthytest.model.WeatherReportModel
import sagar.wellthytest.utils.AppConstants.ACCESS_KEY
import javax.inject.Singleton

@Singleton
class WeatherReportRepository(api: ApiInterface) {

    private val api: ApiInterface = api

    fun getWeatherReport(city: String): LiveData<ApiResponse<WeatherReportModel>> {
        val liveData = MutableLiveData<ApiResponse<WeatherReportModel>>() //live data to observe response in view
        liveData.value = ApiResponse(ApiResponse.Status.LOADING)
        val call = api.getWeatherReport(ACCESS_KEY, city)
        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                liveData.value = ApiResponse(ApiResponse.Status.ERROR)
            }
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null){
                    //convert json to weatherReport model
                    val weatherReportModel = WeatherReportModel().jsonToWeatherReportModel(
                            JSONObject(response.body()!!.asJsonObject.toString())
                    )
                    liveData.value = ApiResponse(weatherReportModel)
                }
            }
        })
        return liveData
    }

}