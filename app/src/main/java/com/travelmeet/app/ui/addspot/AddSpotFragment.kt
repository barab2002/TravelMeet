package com.travelmeet.app.ui.addspot

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.travelmeet.app.R
import com.travelmeet.app.databinding.FragmentAddSpotBinding
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.util.Resource
import java.io.File
import java.util.Locale

class AddSpotFragment : Fragment() {

    private var _binding: FragmentAddSpotBinding? = null
    private val binding get() = _binding!!
    private val spotViewModel: SpotViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var hasLocation: Boolean = false
    private var currentLocationName: String? = null

    // Gallery picker
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivPreview.setImageURI(it)
            binding.ivPreview.visibility = View.VISIBLE
            binding.tvAddImageHint.visibility = View.GONE
        }
    }

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri
            binding.ivPreview.setImageURI(cameraImageUri)
            binding.ivPreview.visibility = View.VISIBLE
            binding.tvAddImageHint.visibility = View.GONE
        }
    }

    // Permission launchers
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(requireContext(), R.string.camera_permission_rationale, Toast.LENGTH_SHORT).show()
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) fetchLocation()
        else Toast.makeText(requireContext(), R.string.location_permission_rationale, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSpotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupClickListeners()
        observeAddSpotState()

        // Auto-fetch location on open if permission already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        }
    }

    private fun setupClickListeners() {
        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                launchCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnGetLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        binding.btnSave.setOnClickListener {
            saveSpot()
        }
    }

    private fun launchCamera() {
        val imageFile = File(requireContext().cacheDir, "images").apply { mkdirs() }
        val file = File(imageFile, "camera_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        cameraLauncher.launch(cameraImageUri)
    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    hasLocation = true

                    // Reverse geocode to get address
                    val addressText = getAddressFromCoordinates(currentLatitude, currentLongitude)
                    currentLocationName = addressText
                    binding.tvLocationCoords.text = addressText
                        ?: String.format("%.6f, %.6f", currentLatitude, currentLongitude)
                    binding.tvLocationLabel.text = getString(R.string.current_location)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Could not get location. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), R.string.location_permission_rationale, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val parts = mutableListOf<String>()
                address.thoroughfare?.let { parts.add(it) }
                address.locality?.let { parts.add(it) }
                address.adminArea?.let { parts.add(it) }
                address.countryName?.let { parts.add(it) }
                if (parts.isNotEmpty()) parts.joinToString(", ") else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun saveSpot() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        if (title.isEmpty()) {
            binding.tilTitle.error = "Title is required"
            return
        }
        if (description.isEmpty()) {
            binding.tilDescription.error = "Description is required"
            return
        }
        if (!hasLocation) {
            Toast.makeText(requireContext(), "Please get your location first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.tilTitle.error = null
        binding.tilDescription.error = null

        spotViewModel.addSpot(
            title = title,
            description = description,
            imageUri = selectedImageUri!!,
            latitude = currentLatitude,
            longitude = currentLongitude,
            locationName = currentLocationName
        )
    }

    private fun observeAddSpotState() {
        spotViewModel.addSpotState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Spot added!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
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
