package sagar.wellthytest.di.module

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sagar.wellthytest.WellthyApp
import sagar.wellthytest.api.ApiInterface
import sagar.wellthytest.repository.WeatherReportRepository
import sagar.wellthytest.utils.AppConstants.BASE_URL
import sagar.wellthytest.utils.SharedPreferencesHelper
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return WellthyApp.context
    }

    @Provides
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferencesHelper {
        return SharedPreferencesHelper(context)
    }

    @Provides
    internal fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    internal fun provideRetrofitInstance(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    internal fun provideWeatherReportRepository(
        api: ApiInterface,
    ): WeatherReportRepository {
        return WeatherReportRepository(api)
    }

}