package com.travelmeet.app.ui.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.FragmentFeedBinding
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.util.PlacesProvider
import com.travelmeet.app.util.Resource
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val spotViewModel: SpotViewModel by activityViewModels()
    private lateinit var spotAdapter: SpotAdapter

    private lateinit var placesClient: PlacesClient
    private val locationPredictions = mutableListOf<AutocompletePrediction>()
    private var locationPlaceSelected: Boolean = false
    private var referenceLat: Double? = null
    private var referenceLng: Double? = null

    private var commentDialog: androidx.appcompat.app.AlertDialog? = null
    private var commentInput: com.google.android.material.textfield.TextInputEditText? = null
    private var pendingCommentSpotId: String? = null
    private var hasDeliveredInitialFeed = false
    private var hasCompletedInitialSync = false
    private var shouldResetOnResume = false
    private var shouldPrefetchAvatarsBeforeReveal = true
    private var avatarPrefetchInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showFeedOverlayAndResetState()
        setupRecyclerView()
        setupSwipeRefresh()
        setupToolbar()
        observeSpots()
        observeCommentState()

        // Initialize Places client once per fragment lifecycle
        placesClient = PlacesProvider.getClient(requireContext())

        // Initial sync
        spotViewModel.syncSpots()
    }

    override fun onResume() {
        super.onResume()
        if (shouldResetOnResume) {
            showFeedOverlayAndResetState()
            spotViewModel.syncSpots()
            shouldResetOnResume = false
        }
    }

    override fun onPause() {
        super.onPause()
        shouldResetOnResume = true
    }

    private fun setupRecyclerView() {
        spotAdapter = SpotAdapter(
            onItemClick = { spot ->
                val action = FeedFragmentDirections.actionFeedFragmentToSpotDetailFragment(spot.id)
                findNavController().navigate(action)
            },
            onLikeClick = { spot ->
                spotViewModel.toggleLike(spot.id)
            },
            onCommentClick = { spot ->
                showAddCommentDialog(spot)
            },
            onSaveClick = { spot ->
                spotViewModel.toggleSave(spot.id)
            }
        )
        binding.rvSpots.apply {
            adapter = spotAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.surface)
        binding.swipeRefresh.setOnRefreshListener {
            spotViewModel.syncSpots()
        }
    }

    private fun observeSpots() {
        spotViewModel.feedSpots.observe(viewLifecycleOwner) { spots ->
            spotAdapter.submitList(spots)
            binding.emptyState.visibility = if (spots.isEmpty()) View.VISIBLE else View.GONE
            binding.rvSpots.visibility = if (spots.isEmpty()) View.GONE else View.VISIBLE
            if (shouldPrefetchAvatarsBeforeReveal) {
                startAvatarPrefetch(spots)
            } else {
                hasDeliveredInitialFeed = true
                hideInitialOverlayIfReady()
            }
        }

        // Observe sync state
        spotViewModel.syncState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.playAnimation()
                    }
                }
                is Resource.Success -> {
                    binding.progressBar.cancelAnimation()
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    hasCompletedInitialSync = true
                    hideInitialOverlayIfReady()
                }
                is Resource.Error -> {
                    binding.progressBar.cancelAnimation()
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                    hasCompletedInitialSync = true
                    hideInitialOverlayIfReady()
                }
            }
        }
    }

    private fun hideInitialOverlayIfReady() {
        if (hasDeliveredInitialFeed && hasCompletedInitialSync) {
            binding.feedInitialProgressBar.cancelAnimation()
            binding.feedInitialOverlay.visibility = View.GONE
        }
    }

    private fun showFeedOverlayAndResetState() {
        hasDeliveredInitialFeed = false
        hasCompletedInitialSync = false
        shouldPrefetchAvatarsBeforeReveal = true
        avatarPrefetchInProgress = false
        binding.feedInitialOverlay.bringToFront()
        binding.feedInitialOverlay.visibility = View.VISIBLE
        binding.feedInitialProgressBar.playAnimation()
    }

    private fun observeCommentState() {
        spotViewModel.commentState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            when (state) {
                is Resource.Loading -> {
                    commentInput?.isEnabled = false
                    view?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)?.isEnabled = false
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), R.string.comments, Toast.LENGTH_SHORT).show()
                    commentDialog?.dismiss()
                    commentDialog = null
                    commentInput = null
                    pendingCommentSpotId = null
                    spotViewModel.resetCommentState()
                }
                is Resource.Error -> {
                    commentInput?.isEnabled = true
                    view?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)?.isEnabled = true
                    Toast.makeText(requireContext(), state.message ?: getString(R.string.error), Toast.LENGTH_LONG).show()
                    spotViewModel.resetCommentState()
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_feed)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_sort -> {
                    showSortSheet()
                    true
                }
                R.id.action_filter -> {
                    showFilterSheet()
                    true
                }
                else -> false
            }
        }
    }

    private fun showSortSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_sort_filter, binding.root as ViewGroup, false)

        val sortOption = spotViewModel.sortOption.value ?: SpotSortOption.DEFAULT

        val chipGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.sortChipGroup)
        chipGroup.removeAllViews()
        SpotSortOption.OPTIONS.forEach { option ->
            val chip = layoutInflater.inflate(R.layout.item_filter_chip, chipGroup, false) as com.google.android.material.chip.Chip
            chip.text = getString(option.labelRes)
            chip.isCheckable = true
            chip.isChecked = option == sortOption
            chip.setOnClickListener {
                // If a location filter is active, confirm before removing it
                if (referenceLat != null && referenceLng != null) {
                    val previousChecked = chipGroup.checkedChipId
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.remove_location_filter_title)
                        .setMessage(R.string.remove_location_filter_message)
                        .setNegativeButton(R.string.cancel) { _, _ ->
                            // Revert to previous selection
                            for (i in 0 until chipGroup.childCount) {
                                val child = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                                child.isChecked = child.id == previousChecked
                            }
                        }
                        .setPositiveButton(R.string.ok) { _, _ ->
                            // Clear location filter in ViewModel
                            referenceLat = null
                            referenceLng = null
                            spotViewModel.setFilters(null, null, null, null, null, null)
                            // Allow this chip to be the only checked one
                            for (i in 0 until chipGroup.childCount) {
                                val childChip = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                                childChip.isChecked = childChip == chip
                            }
                        }
                        .show()
                } else {
                    for (i in 0 until chipGroup.childCount) {
                        val childChip = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                        childChip.isChecked = childChip == chip
                    }
                }
            }
            chipGroup.addView(chip)
        }

        val clearButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonClear)
        val applyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonApply)

        clearButton.setOnClickListener {
            // Reset selection to default sort
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                chip.isChecked = chip.text == getString(SpotSortOption.DEFAULT.labelRes)
            }
            spotViewModel.setSortOption(SpotSortOption.DEFAULT)
        }

        applyButton.setOnClickListener {
            var selected = SpotSortOption.DEFAULT
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                if (chip.isChecked) {
                    val label = chip.text.toString()
                    selected = SpotSortOption.OPTIONS.firstOrNull { getString(it.labelRes) == label } ?: SpotSortOption.DEFAULT
                    break
                }
            }
            spotViewModel.setSortOption(selected)
            dialog.dismiss()
        }

        // Hide search and location views for pure sort sheet
        view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.searchInputLayout)?.visibility = View.GONE
        view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_filter_by_location)?.visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.distanceLimitRow)?.visibility = View.GONE

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showFilterSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_sort_filter, binding.root as ViewGroup, false)

        // Hide sort chips for filter sheet
        view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.sortChipGroup).visibility = View.GONE

        val searchInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.searchInput)
        val locationInput = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.et_filter_location)
        val distanceValueInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_distance_value)
        val distanceUnitInput = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.et_distance_unit)

        // Pre-fill from ViewModel if we have previous filters
        searchInput.setText(spotViewModel.getSearchQuery() ?: "")
        locationInput.setText(spotViewModel.getLastLocationName() ?: "", false)
        distanceValueInput.setText(spotViewModel.getLastDistanceRaw() ?: "")
        val lastUnit = spotViewModel.getLastDistanceUnit()
        if (!lastUnit.isNullOrEmpty()) {
            distanceUnitInput.setText(lastUnit, false)
        }

        // Simple km/m unit suggestions
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOf("km", "m"))
        distanceUnitInput.setAdapter(unitAdapter)

        setupLocationAutocomplete(locationInput)

        val clearButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonClear)
        val applyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonApply)

        clearButton.setOnClickListener {
            searchInput.text?.clear()
            locationInput.text?.clear()
            distanceValueInput.text?.clear()
            distanceUnitInput.text?.clear()
            locationPlaceSelected = false
            referenceLat = null
            referenceLng = null
            spotViewModel.setFilters(null, null, null, null, null, null)
            spotViewModel.setSearchQuery(null)
        }

        applyButton.setOnClickListener {
            val searchText = searchInput.text?.toString()?.trim()?.ifEmpty { null }
            val locationText = locationInput.text?.toString()?.trim()?.ifEmpty { null }
            val rawValue = distanceValueInput.text?.toString()?.trim()?.ifEmpty { null }
            val unit = distanceUnitInput.text?.toString()?.trim()?.lowercase()?.ifEmpty { null }

            val maxDistanceMeters = if (!rawValue.isNullOrEmpty()) {
                val numeric = rawValue.toDoubleOrNull()
                if (numeric != null && numeric > 0) {
                    when (unit) {
                        "km" -> numeric * 1000.0
                        "m" -> numeric
                        else -> numeric * 1000.0
                    }
                } else null
            } else null

            spotViewModel.setFilters(
                latitude = referenceLat,
                longitude = referenceLng,
                maxDistanceMeters = maxDistanceMeters,
                locationName = locationText,
                rawDistance = rawValue,
                distanceUnit = unit
            )

            // If a reference location is set, reset sort to default so UI matches distance-based sorting
            if (referenceLat != null && referenceLng != null) {
                spotViewModel.setSortOption(SpotSortOption.DEFAULT)
            }

            spotViewModel.setSearchQuery(searchText)

            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupLocationAutocomplete(autoComplete: com.google.android.material.textfield.MaterialAutoCompleteTextView) {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoComplete.setAdapter(adapter)

        autoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: return
                if (query.length < 2 || locationPlaceSelected) {
                    locationPlaceSelected = false
                    return
                }

                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        locationPredictions.clear()
                        locationPredictions.addAll(response.autocompletePredictions)
                        adapter.clear()
                        adapter.addAll(locationPredictions.map { it.getFullText(null).toString() })
                        adapter.notifyDataSetChanged()
                        if (locationPredictions.isNotEmpty() && autoComplete.isAttachedToWindow) {
                            autoComplete.showDropDown()
                        }
                    }
            }
        })

        autoComplete.setOnItemClickListener { _, _, position, _ ->
            if (position >= locationPredictions.size) return@setOnItemClickListener
            val prediction = locationPredictions[position]
            locationPlaceSelected = true

            val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
            val fetchRequest = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

            placesClient.fetchPlace(fetchRequest)
                .addOnSuccessListener { fetchResponse ->
                    val place = fetchResponse.place
                    val latLng = place.latLng
                    if (latLng != null) {
                        referenceLat = latLng.latitude
                        referenceLng = latLng.longitude
                    }
                }
        }
    }

    private fun showAddCommentDialog(spot: SpotEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        val input = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_comment)
        val til = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_comment)
        val btnSubmit = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancel)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            val text = input.text?.toString()?.trim().orEmpty()
            if (text.length < 2) {
                til.error = getString(R.string.add_comment_hint)
                return@setOnClickListener
            }
            til.error = null
            pendingCommentSpotId = spot.id
            commentInput = input
            view?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)?.isEnabled = false
            spotViewModel.addComment(spot.id, text)
        }

        dialog.setOnDismissListener {
            commentDialog = null
            commentInput = null
            pendingCommentSpotId = null
        }

        dialog.show()
        commentDialog = dialog
    }

    private fun startAvatarPrefetch(spots: List<SpotEntity>) {
        if (!shouldPrefetchAvatarsBeforeReveal) {
            hasDeliveredInitialFeed = true
            hideInitialOverlayIfReady()
            return
        }

        if (avatarPrefetchInProgress) return

        val urls = spots.mapNotNull { it.userPhotoUrl?.takeIf { url -> url.isNotBlank() } }.toSet()
        if (urls.isEmpty()) {
            shouldPrefetchAvatarsBeforeReveal = false
            hasDeliveredInitialFeed = true
            hideInitialOverlayIfReady()
            return
        }

        avatarPrefetchInProgress = true
        var remaining = urls.size

        fun handleOneFinished() {
            remaining--
            if (remaining <= 0) {
                _binding?.root?.post {
                    avatarPrefetchInProgress = false
                    shouldPrefetchAvatarsBeforeReveal = false
                    hasDeliveredInitialFeed = true
                    hideInitialOverlayIfReady()
                }
            }
        }

        val picasso = Picasso.get()
        urls.forEach { url ->
            picasso.load(url).fetch(object : Callback {
                override fun onSuccess() = handleOneFinished()
                override fun onError(e: Exception?) = handleOneFinished()
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
