package com.example.testweatherapi.presentation.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.testweatherapi.databinding.FragmentWeatherListBinding
import com.example.testweatherapi.presentation.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherListFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels()

    private var _binding: FragmentWeatherListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WeatherListAdapter
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.loadWeatherByLocation(requireActivity(), requireContext())
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
        _binding = FragmentWeatherListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WeatherListAdapter()
        binding.recyclerView.adapter = adapter
        binding.forecastButton.setOnClickListener {
            viewModel.loadWeatherByLocation(
                requireActivity(),
                requireContext(),
                binding.editLocation.text.toString()
            )
        }
        viewModel.apply {
            locality.observe(viewLifecycleOwner) {
                binding.locality.text = it
            }
            countryName.observe(viewLifecycleOwner) {
                binding.countryName.text = it
            }
            result.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
        showDefaultForecast()
    }

    private fun showDefaultForecast() {
        viewModel.loadWeatherByLocation(requireActivity(), requireContext())
        viewModel.result.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            binding.loadingText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
