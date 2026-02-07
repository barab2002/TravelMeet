package com.travelmeet.app.ui.addspot

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.travelmeet.app.R
import com.travelmeet.app.databinding.FragmentAddSpotBinding
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.util.Resource
import java.io.File
import java.io.IOException
import java.util.Locale

class AddSpotFragment : Fragment() {

    private var _binding: FragmentAddSpotBinding? = null
    private val binding get() = _binding!!
    private val spotViewModel: SpotViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private var cameraImageUri: Uri? = null
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var hasLocation: Boolean = false
    private var currentLocationName: String? = null
    private var placeSelected: Boolean = false
    private val predictions = mutableListOf<AutocompletePrediction>()

    // Gallery picker (multi-select)
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.addAll(uris)
            imageAdapter.notifyDataSetChanged()
        }
    }

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { 
                selectedImageUris.add(it)
                imageAdapter.notifyDataSetChanged()
            }
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
        placesClient = Places.createClient(requireContext())
        setupRecyclerView()
        setupClickListeners()
        setupPlacesAutocomplete()
        observeAddSpotState()

        // Auto-fetch location on open if permission already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(selectedImageUris)
        binding.rvImages.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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

    private fun setupPlacesAutocomplete() {
        val autoComplete = binding.etLocationManual
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoComplete.setAdapter(adapter)

        autoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: return
                if (query.length < 2 || placeSelected) {
                    placeSelected = false
                    return
                }

                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        predictions.clear()
                        predictions.addAll(response.autocompletePredictions)
                        adapter.clear()
                        adapter.addAll(predictions.map { it.getFullText(null).toString() })
                        adapter.notifyDataSetChanged()
                        if (predictions.isNotEmpty() && autoComplete.isAttachedToWindow) {
                            autoComplete.showDropDown()
                        }
                    }
            }
        })

        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (position >= predictions.size) return@OnItemClickListener
            val prediction = predictions[position]
            placeSelected = true

            val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
            val fetchRequest = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

            placesClient.fetchPlace(fetchRequest)
                .addOnSuccessListener { fetchResponse ->
                    val place = fetchResponse.place
                    val latLng = place.latLng
                    if (latLng != null) {
                        currentLatitude = latLng.latitude
                        currentLongitude = latLng.longitude
                        hasLocation = true
                        currentLocationName = prediction.getFullText(null).toString()
                        binding.tvLocationCoords.text = String.format(
                            "%.6f, %.6f", currentLatitude, currentLongitude
                        )
                        binding.tvLocationLabel.text = place.name ?: currentLocationName
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Could not fetch place details", Toast.LENGTH_SHORT).show()
                }
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
        val manualLocation = binding.etLocationManual.text.toString().trim()

        if (selectedImageUris.isEmpty()) {
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

        if (manualLocation.isNotEmpty() && !hasLocation) {
            // User typed a location but didn't select from autocomplete — use Geocoder as fallback
            getCoordinatesFromLocationName(manualLocation)
            if (!hasLocation) {
                return
            }
        } else if (manualLocation.isEmpty() && !hasLocation) {
            Toast.makeText(requireContext(), "Please get your location first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.tilTitle.error = null
        binding.tilDescription.error = null

        spotViewModel.addSpot(
            title = title,
            description = description,
            imageUris = selectedImageUris,
            latitude = currentLatitude,
            longitude = currentLongitude,
            locationName = if (manualLocation.isNotEmpty()) currentLocationName ?: manualLocation else currentLocationName
        )
    }

    private fun getCoordinatesFromLocationName(locationName: String) {
        val geocoder = Geocoder(requireContext())
        try {
            val addressList = geocoder.getFromLocationName(locationName, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                currentLatitude = address.latitude
                currentLongitude = address.longitude
                hasLocation = true

                binding.tvLocationCoords.text = String.format(
                    "%.6f, %.6f", currentLatitude, currentLongitude
                )
                binding.tvLocationLabel.text = locationName
            } else {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeAddSpotState() {
        spotViewModel.addSpotState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressBar.playAnimation()
                    binding.btnSave.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.cancelAnimation()
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Spot added!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.progressBar.cancelAnimation()
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

    inner class ImageAdapter(private val uris: List<Uri>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        inner class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val imageView = LayoutInflater.from(parent.context).inflate(R.layout.item_image_preview, parent, false) as ImageView
            return ImageViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.imageView.setImageURI(uris[position])
        }

        override fun getItemCount() = uris.size
    }
}
