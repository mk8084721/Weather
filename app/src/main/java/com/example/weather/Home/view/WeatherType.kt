package com.example.weather.Home.view

import android.content.Context


import androidx.annotation.DrawableRes
import com.example.weather.R

sealed class WeatherType(
    val weatherDesc: String,
    @DrawableRes val iconRes: Int,
) {
    class ClearSky(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_clear_sky_day),
        iconRes = R.drawable.ic_sunny
    )

    class MainlyClear(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_clear_sky_day),
        iconRes = R.drawable.ic_cloudy
    )

    class PartlyCloudy(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_few_clouds_day),
        iconRes = R.drawable.ic_cloudy
    )

    class Overcast(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_few_clouds_day),
        iconRes = R.drawable.ic_cloudy
    )

    class Foggy(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_scattered_clouds_day),
        iconRes = R.drawable.ic_very_cloudy
    )

    class DepositingRimeFog(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_mist_day),
        iconRes = R.drawable.ic_very_cloudy
    )

    class LightDrizzle(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_few_clouds_day),
        iconRes = R.drawable.ic_rainshower
    )

    class ModerateDrizzle(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_scattered_clouds_day),
        iconRes = R.drawable.ic_rainshower
    )

    class DenseDrizzle(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_mist_day),
        iconRes = R.drawable.ic_rainshower
    )

    class LightFreezingDrizzle(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_clear_sky_day),
        iconRes = R.drawable.ic_snowyrainy
    )

    class DenseFreezingDrizzle(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_few_clouds_day),
        iconRes = R.drawable.ic_snowyrainy
    )

    class SlightRain(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_scattered_clouds_day),
        iconRes = R.drawable.ic_rainy
    )

    class ModerateRain(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_mist_day),
        iconRes = R.drawable.ic_rainy
    )

    class HeavyRain(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_rain_day),
        iconRes = R.drawable.ic_rainy
    )

    class HeavyFreezingRain(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_night),
        iconRes = R.drawable.ic_snowyrainy
    )

    class SlightSnowFall(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_night),
        iconRes = R.drawable.ic_snowy
    )

    class ModerateSnowFall(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_night),
        iconRes = R.drawable.ic_heavysnow
    )

    class HeavySnowFall(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_night),
        iconRes = R.drawable.ic_heavysnow
    )

    class SnowGrains(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_night),
        iconRes = R.drawable.ic_heavysnow
    )

    class SlightRainShowers(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_shower_rain_day),
        iconRes = R.drawable.ic_rainshower
    )

    class ModerateRainShowers(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_shower_rain_day),
        iconRes = R.drawable.ic_rainshower
    )

    class ViolentRainShowers(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_shower_rain_day),
        iconRes = R.drawable.ic_rainshower
    )

    class SlightSnowShowers(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_day),
        iconRes = R.drawable.ic_snowy
    )

    class HeavySnowShowers(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_snow_day),
        iconRes = R.drawable.ic_snowy
    )

    class ModerateThunderstorm(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_thunderstorm_day),
        iconRes = R.drawable.ic_thunder
    )

    class SlightHailThunderstorm(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_thunderstorm_day),
        iconRes = R.drawable.ic_rainythunder
    )

    class HeavyHailThunderstorm(context: Context) : WeatherType(
        weatherDesc = context.getString(R.string.weather_rain_day),
        iconRes = R.drawable.ic_rainythunder
    )

    companion object {
        fun fromWeatherID(code: String, context: Context): WeatherType {
            return when (code) {
                "01d" -> ClearSky(context)
                "01n" -> ClearSky(context)
                "02d" -> PartlyCloudy(context)
                "02n" -> PartlyCloudy(context)
                "03d" -> Foggy(context)
                "03n" -> Foggy(context)
                "04d" -> Foggy(context)
                "04n" -> Foggy(context)
                "09d" -> ModerateRainShowers(context)
                "09n" -> ModerateRainShowers(context)
                "10d" -> HeavyRain(context)
                "10n" -> HeavyRain(context)
                "11d" -> ModerateThunderstorm(context)
                "11n" -> ModerateThunderstorm(context)
                "13d" -> HeavySnowShowers(context)
                "13n" -> HeavySnowShowers(context)
                "50d" -> DepositingRimeFog(context)
                "50n" -> DepositingRimeFog(context)
                else -> ClearSky(context)
            }
        }
    }
}


    /*companion object {
        fun fromWMO(code: Int): WeatherType {
            return when(code) {
                0 -> ClearSky
                1 -> MainlyClear
                2 -> PartlyCloudy
                3 -> Overcast
                45 -> Foggy
                48 -> DepositingRimeFog
                51 -> LightDrizzle
                53 -> ModerateDrizzle
                55 -> DenseDrizzle
                56 -> LightFreezingDrizzle
                57 -> DenseFreezingDrizzle
                61 -> SlightRain
                63 -> ModerateRain
                65 -> HeavyRain
                66 -> LightFreezingDrizzle
                67 -> HeavyFreezingRain
                71 -> SlightSnowFall
                73 -> ModerateSnowFall
                75 -> HeavySnowFall
                77 -> SnowGrains
                80 -> SlightRainShowers
                81 -> ModerateRainShowers
                82 -> ViolentRainShowers
                85 -> SlightSnowShowers
                86 -> HeavySnowShowers
                95 -> ModerateThunderstorm
                96 -> SlightHailThunderstorm
                99 -> HeavyHailThunderstorm
                else -> ClearSky
            }
        }
    }*/

/*
{
    "01d": "clear sky",
    "01n": "clear sky (night)",
    "02d": "few clouds",
    "02n": "few clouds (night)",
    "03d": "scattered clouds",
    "03n": "scattered clouds (night)",
    "04d": "broken clouds",
    "04n": "broken clouds (night)",
    "09d": "shower rain",
    "09n": "shower rain (night)",
    "10d": "rain",
    "10n": "rain (night)",
    "11d": "thunderstorm",
    "11n": "thunderstorm (night)",
    "13d": "snow",
    "13n": "snow (night)",
    "50d": "mist",
    "50n": "mist (night)"
}
*/
