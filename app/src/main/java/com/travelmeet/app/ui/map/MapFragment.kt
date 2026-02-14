package com.travelmeet.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.FragmentMapBinding
import com.travelmeet.app.ui.viewmodel.AuthViewModel
import com.travelmeet.app.ui.viewmodel.SpotViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val spotViewModel: SpotViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val spotMarkerMap = mutableMapOf<String, SpotEntity>()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            enableMyLocation()
            centerOnUser()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fabMyLocation.setOnClickListener {
            requestLocationAndCenter()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Map styling for dark theme
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false

        // Set custom info window adapter
        map.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))

        // Set marker click listener
        map.setOnInfoWindowClickListener { marker ->
            val spot = marker.tag as? SpotEntity
            spot?.let {
                val action = MapFragmentDirections
                    .actionMapFragmentToSpotDetailFragment(it.id)
                findNavController().navigate(action)
            }
        }

        // Enable location if permission granted
        if (hasLocationPermission()) {
            enableMyLocation()
        }

        // Observe spots and add markers
        observeSpots()
    }

    private fun observeSpots() {
        spotViewModel.allSpots.observe(viewLifecycleOwner) { spots ->
            binding.mapProgressBar.cancelAnimation()
            binding.mapProgressBar.visibility = View.GONE
            googleMap?.let { map ->
                map.clear()
                spotMarkerMap.clear()

                spots.forEach { spot ->
                    val position = LatLng(spot.latitude, spot.longitude)
                    val markerColor = if (spot.userId == authViewModel.currentUserId) {
                        BitmapDescriptorFactory.HUE_AZURE
                    } else {
                        BitmapDescriptorFactory.HUE_ROSE
                    }

                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(spot.title)
                            .snippet("by ${spot.username}")
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                    )
                    marker?.tag = spot
                    spotMarkerMap[spot.id] = spot
                }

                // Zoom to show all markers if spots exist
                if (spots.isNotEmpty()) {
                    val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
                    spots.forEach { spot ->
                        builder.include(LatLng(spot.latitude, spot.longitude))
                    }
                    try {
                        val bounds = builder.build()
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        )
                    } catch (_: Exception) {
                        // Single point or no points
                    }
                }
            }
        }
    }

    private fun requestLocationAndCenter() {
        if (hasLocationPermission()) {
            centerOnUser()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun centerOnUser() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(
                requireContext(),
                R.string.location_permission_rationale,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun enableMyLocation() {
        try {
            googleMap?.isMyLocationEnabled = true
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
