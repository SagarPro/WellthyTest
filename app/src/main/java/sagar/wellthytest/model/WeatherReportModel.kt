package sagar.wellthytest.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.lang.Exception

class WeatherReportModel {

    @SerializedName("temperature")
    @Expose
    var temperature: Int? = null
    @SerializedName("weather_code")
    @Expose
    var weather_code: Int? = null
    @SerializedName("weather_icons")
    @Expose
    var weather_icons: List<String>? = null
    @SerializedName("weather_descriptions")
    @Expose
    var weather_descriptions: List<String>? = null

    constructor(){}

    constructor(temperature: Int?, weather_code: Int?, weather_icons: List<String>?, weather_descriptions: List<String>?) {
        this.temperature = temperature
        this.weather_code = weather_code
        this.weather_icons = weather_icons
        this.weather_descriptions = weather_descriptions
    }

    fun jsonToWeatherReportModel(jsonObject: JSONObject): WeatherReportModel {

        if (jsonObject.has("current")){

            if (jsonObject.get("current") != "null"){
                val currentWeatherJson = jsonObject.getJSONObject("current")

                var temp: Int? = null
                try {
                    temp = currentWeatherJson.getInt("temperature")
                } catch (e: Exception){}

                var weatherCode: Int? = null
                try {
                    weatherCode = currentWeatherJson.getInt("weather_code")
                } catch (e: Exception){}

                var weatherIcons:List<String>? = null
                if (currentWeatherJson.has("weather_icons")){
                    if (currentWeatherJson.get("weather_icons") != "null"){
                        val icons = currentWeatherJson.getJSONArray("weather_icons")
                        weatherIcons = ArrayList()
                        for (i in 0 until icons.length())
                            weatherIcons.add(icons.getString(i))
                    }
                }

                var weatherDesc:List<String>? = null
                if (currentWeatherJson.has("weather_descriptions")){
                    if (currentWeatherJson.get("weather_descriptions") != "null"){
                        val desc = currentWeatherJson.getJSONArray("weather_descriptions")
                        weatherDesc = ArrayList()
                        for (i in 0 until desc.length())
                            weatherDesc.add(desc.getString(i))
                    }
                }

                return WeatherReportModel(
                        temp,
                        weatherCode,
                        weatherIcons,
                        weatherDesc
                )
            }
        }

        return WeatherReportModel()

    }

}