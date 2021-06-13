package sagar.wellthytest.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @GET("current")
    fun getWeatherReport(
            @Query("access_key") access_key: String,
            @Query("query") query: String,
    ): Call<JsonObject>

}