package com.example.testweatherapi.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testweatherapi.databinding.ListItemWeatherBinding
import com.example.testweatherapi.data.remote.WeatherElement
import kotlin.math.roundToInt


class WeatherListAdapter : ListAdapter<WeatherElement, WeatherListAdapter.WeatherViewHolder>(
    DiffCallBack
) {
    companion object DiffCallBack: DiffUtil.ItemCallback<WeatherElement>() {
        override fun areItemsTheSame(oldItem: WeatherElement, newItem: WeatherElement): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: WeatherElement, newItem: WeatherElement): Boolean {
            return oldItem == newItem
        }
    }

    class WeatherViewHolder(
            private val binding: ListItemWeatherBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(element: WeatherElement) {
                binding.averageTemp.text = "${((element.temperature2MMax+element.temperature2MMin)/2).roundToInt()} °C"
                binding.maxTemp.text = "Max temp: ${element.temperature2MMax.roundToInt()} °C"
                binding.minTemp.text = "Min temp: ${element.temperature2MMin.roundToInt()}°C"
                binding.date.text = element.time
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherViewHolder(
            ListItemWeatherBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val element = getItem(position)
        holder.bind(element)
    }
}