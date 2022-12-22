package com.example.testweatherapi.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/forecast?daily=temperature_2m_max,temperature_2m_min&timezone=Europe%2FBerlin")
    suspend fun getWeather(@Query("latitude") latitude: String,
                           @Query("longitude") longitude: String,
                           @Query("start_date") start_date: String,
                           @Query("end_date") end_date: String): WeatherList
}