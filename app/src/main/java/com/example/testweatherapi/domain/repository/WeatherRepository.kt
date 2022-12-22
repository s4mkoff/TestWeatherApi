package com.example.testweatherapi.domain.repository

import com.example.testweatherapi.data.remote.WeatherElement


interface WeatherRepository {
    suspend fun getWeather(latitude: String,
                           longitude: String,
                           start_date: String,
                           end_date: String): List<WeatherElement>
}