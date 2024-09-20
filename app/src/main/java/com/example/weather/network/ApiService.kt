package com.example.weather.network

import com.example.weather.model.CurrentWeather
import de.hdodenhof.circleimageview.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService{
    @GET("weather")
    suspend fun getCurrentWeather(@Query("lat") lat: Double,
                                  @Query("lon") lon: Double,
                                  @Query("appid") apiKey: String = "806997291266b80b0594146105e26982"
    ): CurrentWeather
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