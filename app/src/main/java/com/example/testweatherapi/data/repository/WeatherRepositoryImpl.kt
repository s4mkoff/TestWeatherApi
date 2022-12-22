package com.example.testweatherapi.data.repository

import com.example.testweatherapi.data.remote.WeatherApi
import com.example.testweatherapi.domain.repository.WeatherRepository
import com.example.testweatherapi.data.remote.WeatherElement
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
): WeatherRepository {
    override suspend fun getWeather(
        latitude: String,
        longitude: String,
        start_date: String,
        end_date: String
    ): List<WeatherElement> {
        val creatingWeatherElement = mutableListOf<WeatherElement>()
        var weatherData = api.getWeather(
            latitude = latitude,
            longitude = longitude,
            start_date = start_date,
            end_date = end_date)

        repeat(weatherData.daily.time.size) {
            creatingWeatherElement.add(WeatherElement(
                it.toString(),
                weatherData.daily.time[it],
                weatherData.daily.temperature2MMax[it],
                weatherData.daily.temperature2MMin[it])
            )
        }
        return creatingWeatherElement
    }
}
