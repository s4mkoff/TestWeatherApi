package com.example.testweatherapi.data.remote

import com.squareup.moshi.Json

data class WeatherList (
    val latitude: Double,
    val longitude: Double,

    @Json(name = "generationtime_ms")
    val generationtimeMS: Double,

    @Json(name = "utc_offset_seconds")
    val utcOffsetSeconds: Long,

    val timezone: String,

    @Json(name = "timezone_abbreviation")
    val timezoneAbbreviation: String,

    val elevation: Double,

    @Json(name = "daily_units")
    val dailyUnits: DailyUnits,

    val daily: Daily
)

data class Daily (
    val time: List<String>,

    @Json(name = "temperature_2m_max")
    val temperature2MMax: List<Double>,

    @Json(name = "temperature_2m_min")
    val temperature2MMin: List<Double>
)

data class DailyUnits (
    val time: String,

    @Json(name = "temperature_2m_max")
    val temperature2MMax: String
)

data class WeatherElement(
    val id: String,
    val time: String,
    val temperature2MMax: Double,
    val temperature2MMin: Double
)