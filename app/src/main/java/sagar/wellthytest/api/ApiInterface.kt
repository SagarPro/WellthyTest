package sagar.wellthytest.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @GET
    fun getWeatherReport(
        @Url url: String
    ): Call<JsonObject>

}