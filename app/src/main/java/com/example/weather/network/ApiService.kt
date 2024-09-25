package com.example.weather.network

import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService{
    @GET("weather")
    suspend fun getCurrentWeather(@Query("lon") lon: Float,
                                  @Query("lat") lat: Float,
                                  @Query("lang") lang: String = "en",
                                  @Query("units") units: String ="metric",
                                  @Query("appid") apiKey: String = "806997291266b80b0594146105e26982"
    ): CurrentWeather
    @GET("forecast")
    suspend fun getWeatherForecast(@Query("lon") lon: Float,
                                   @Query("lat") lat: Float,
                                   @Query("lang") lang: String = "en",
                                   @Query("units") units: String ="metric",
                                   @Query("appid") apiKey: String = "806997291266b80b0594146105e26982"
    ): ForecastWeather
    //api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid={API key}
}

object RetrofitHelper{
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    val retrofitInstance = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
}

object API{
    val retrofitService: ApiService by lazy {
        RetrofitHelper.retrofitInstance.create(ApiService::class.java)
    }
}