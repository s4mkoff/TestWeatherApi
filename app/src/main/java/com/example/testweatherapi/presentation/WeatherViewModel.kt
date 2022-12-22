package com.example.testweatherapi.presentation

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.*
import com.example.testweatherapi.domain.location.LocationTracker
import com.example.testweatherapi.domain.repository.WeatherRepository
import com.example.testweatherapi.data.remote.WeatherElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
): ViewModel() {

    //Variables
    private val _locality = MutableLiveData<String>()
    val locality: LiveData<String> = _locality
    private val _countryName = MutableLiveData<String>()
    val countryName: LiveData<String> = _countryName

    //Get weather from API
    private val _result = MutableLiveData<List<WeatherElement>>()
    val result: LiveData<List<WeatherElement>> = _result

    fun loadWeatherByLocation(activity: Activity, context: Context, city: String? = null) {
        if (isNetworkAvailable(activity)) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val dates = getDate()
            if (!city.isNullOrEmpty()) {
                viewModelScope.launch {
                    val list: List<Address> = geocoder.getFromLocationName(city, 1)
                    val weather = repository.getWeather(
                        list[0].latitude.toString(),
                        list[0].longitude.toString(),
                        dates[0],
                        dates[1]
                    )
                    _countryName.value = list[0].countryName
                    _locality.value = list[0].locality
                    _result.value = weather
                }
            } else {
                viewModelScope.launch {
                    locationTracker.getCurrentLocation()?.let { location ->
                        when(val weather = repository.getWeather(
                            location.latitude.toString(),
                            location.longitude.toString(),
                            dates[0],
                            dates[1]
                        )) {
                            is List<WeatherElement> -> {
                                val list: List<Address> =
                                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                _countryName.value = list[0].countryName
                                _locality.value = list[0].locality
                                _result.value = weather
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, "Enable network", Toast.LENGTH_SHORT).show()
        }
    }

    //Get date
    private fun getDate(): List<String> {
        val dates = mutableListOf<String>()
        val formatter = SimpleDateFormat("y-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        dates.add(formatter.format(calendar.time))
        calendar.add(Calendar.DATE, 15)
        dates.add(formatter.format(calendar.time))
        return dates
    }

    //Check internet activity
    private fun isNetworkAvailable(activity: Activity): Boolean {
        val cm =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val networks = cm.allNetworks
            for (n in networks) {
                val nInfo = cm.getNetworkInfo(n)
                if (nInfo != null && nInfo.isConnected) return true
            }
        } else {
            val networks = cm.allNetworkInfo
            for (nInfo in networks) {
                if (nInfo != null && nInfo.isConnected) return true
            }
        }
        return false
    }
}