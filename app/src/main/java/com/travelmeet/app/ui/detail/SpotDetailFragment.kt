package com.travelmeet.app.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.FragmentSpotDetailBinding
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.ui.viewmodel.WeatherViewModel
import com.travelmeet.app.util.Resource
import com.travelmeet.app.util.TimeUtils

class SpotDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSpotDetailBinding? = null
    private val binding get() = _binding!!
    private val args: SpotDetailFragmentArgs by navArgs()
    private val spotViewModel: SpotViewModel by activityViewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var currentSpot: SpotEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpotDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupMap()
        observeSpot()
        observeWeather()
        observeDeleteState()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.likesContainer.setOnClickListener {
            spotViewModel.toggleLike(args.spotId)
        }

        binding.btnEdit.setOnClickListener {
            val action = SpotDetailFragmentDirections
                .actionSpotDetailFragmentToAddSpotFragment()
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete) { _, _ ->
                    spotViewModel.deleteSpot(args.spotId)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_preview) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isScrollGesturesEnabled = false
        map.uiSettings.isZoomGesturesEnabled = false

        currentSpot?.let { spot ->
            val location = LatLng(spot.latitude, spot.longitude)
            map.addMarker(MarkerOptions().position(location).title(spot.title))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        }
    }

    private fun observeSpot() {
        spotViewModel.getSpotById(args.spotId).observe(viewLifecycleOwner) { spot ->
            spot?.let { bindSpotData(it) }
        }
    }

    private fun bindSpotData(spot: SpotEntity) {
        currentSpot = spot

        binding.tvTitle.text = spot.title
        binding.tvDescription.text = spot.description
        binding.tvUsername.text = spot.username
        binding.tvTimestamp.text = TimeUtils.getRelativeTimeString(spot.timestamp)
        binding.tvLikesCount.text = "${spot.likesCount} likes"
        binding.tvLocation.text = spot.locationName ?: String.format(
            "%.4f, %.4f", spot.latitude, spot.longitude
        )

        // Load images
        if (spot.imageUrl.isNotEmpty()) {
            Picasso.get().load(spot.imageUrl).into(binding.ivHeroImage)
        }
        if (!spot.userPhotoUrl.isNullOrEmpty()) {
            Picasso.get().load(spot.userPhotoUrl).into(binding.ivUserAvatar)
        }

        // Show owner actions
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        binding.ownerActions.visibility =
            if (spot.userId == currentUserId) View.VISIBLE else View.GONE

        // Fetch weather for spot location
        weatherViewModel.fetchWeather(spot.latitude, spot.longitude)

        // Update map if ready
        googleMap?.let { map ->
            val location = LatLng(spot.latitude, spot.longitude)
            map.clear()
            map.addMarker(MarkerOptions().position(location).title(spot.title))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        }
    }

    private fun observeWeather() {
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.weatherProgress.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.weatherProgress.visibility = View.GONE
                    resource.data?.let { weather ->
                        binding.tvTemperature.text = "${weather.main.temp.toInt()}°C"
                        binding.tvWeatherTemp.text = "${weather.main.temp.toInt()}°C"
                        binding.tvWeatherDesc.text = weather.weather.firstOrNull()
                            ?.description?.replaceFirstChar { it.uppercase() } ?: ""
                        binding.tvWeatherHumidity.text =
                            getString(R.string.humidity, weather.main.humidity)
                        binding.tvWeatherWind.text =
                            getString(R.string.wind_speed, weather.wind.speed)
                    }
                }
                is Resource.Error -> {
                    binding.weatherProgress.visibility = View.GONE
                    binding.tvWeatherTemp.text = "N/A"
                    binding.tvTemperature.text = "--"
                }
            }
        }
    }

    private fun observeDeleteState() {
        spotViewModel.deleteSpotState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> { /* show loading if desired */ }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Spot deleted", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
