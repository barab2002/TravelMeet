package com.travelmeet.app.ui.detail

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.FragmentSpotDetailBinding
import com.travelmeet.app.ui.feed.ImageSliderAdapter
import com.travelmeet.app.ui.viewmodel.AuthViewModel
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.ui.viewmodel.WeatherViewModel
import com.travelmeet.app.util.Resource
import com.travelmeet.app.util.TimeUtils

class SpotDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSpotDetailBinding? = null
    private val binding get() = _binding!!
    private val args: SpotDetailFragmentArgs by navArgs()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val spotViewModel: SpotViewModel by activityViewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var currentSpot: SpotEntity? = null
    private lateinit var commentsAdapter: CommentsAdapter

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
        setupTabs()
        setupCommentsSection()
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
                .actionSpotDetailFragmentToAddSpotFragment(args.spotId)
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

        // Set up hero image carousel
        val urls = spot.imageUrls.filter { it.isNotBlank() }
        binding.vpHeroImages.adapter = ImageSliderAdapter(urls)

        if (urls.size > 1) {
            binding.tvImageCount.visibility = View.VISIBLE
            binding.tvImageCount.text = "1/${urls.size}"
            binding.dotIndicatorDetail.visibility = View.VISIBLE
            setupDots(binding.dotIndicatorDetail, urls.size, 0)

            binding.vpHeroImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tvImageCount.text = "${position + 1}/${urls.size}"
                    updateDots(binding.dotIndicatorDetail, position)
                }
            })
        } else {
            binding.tvImageCount.visibility = View.GONE
            binding.dotIndicatorDetail.visibility = View.GONE
        }

        if (!spot.userPhotoUrl.isNullOrEmpty()) {
            Picasso.get().load(spot.userPhotoUrl).into(binding.ivUserAvatar)
        }

        // Show owner actions
        val currentUserId = authViewModel.currentUserId
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
                    binding.weatherProgress.playAnimation()
                }
                is Resource.Success -> {
                    binding.weatherProgress.cancelAnimation()
                    binding.weatherProgress.visibility = View.GONE
                    resource.data?.let { weather ->
                        val current = weather.current
                        binding.tvTemperature.text = "${current.temperature.toInt()}°C"
                        binding.tvWeatherTemp.text = "${current.temperature.toInt()}°C"
                        binding.tvWeatherDesc.text = current.description
                        binding.tvWeatherHumidity.text =
                            getString(R.string.humidity, current.humidity)
                        binding.tvWeatherWind.text =
                            getString(R.string.wind_speed, current.windSpeed)
                    }
                }
                is Resource.Error -> {
                    binding.weatherProgress.cancelAnimation()
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
                is Resource.Loading -> {
                    binding.deleteLoadingOverlay.visibility = View.VISIBLE
                    binding.deleteProgressBar.playAnimation()
                }
                is Resource.Success -> {
                    binding.deleteProgressBar.cancelAnimation()
                    binding.deleteLoadingOverlay.visibility = View.GONE
                    Toast.makeText(requireContext(), "Spot deleted", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.deleteProgressBar.cancelAnimation()
                    binding.deleteLoadingOverlay.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupTabs() {
        binding.spotTabs.apply {
            removeAllTabs()
            addTab(newTab().setText(R.string.spot_tab_details))
            addTab(newTab().setText(R.string.spot_tab_comments))
            getTabAt(0)?.select()
        }
        showDetailsTab()
        binding.spotTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) showDetailsTab() else showCommentsTab()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                if (tab.position == 1 && ::commentsAdapter.isInitialized) {
                    binding.rvComments.smoothScrollToPosition(0)
                }
            }
        })
    }

    private fun showDetailsTab() {
        binding.detailsContainer.visibility = View.VISIBLE
        binding.commentsContainer.visibility = View.GONE
    }

    private fun showCommentsTab() {
        binding.detailsContainer.visibility = View.GONE
        binding.commentsContainer.visibility = View.VISIBLE
    }

    private fun setupCommentsSection() {
        commentsAdapter = CommentsAdapter()
        binding.rvComments.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            visibility = View.GONE
        }
        binding.commentsProgress.visibility = View.VISIBLE
        spotViewModel.observeComments(args.spotId).observe(viewLifecycleOwner) { comments ->
            binding.commentsProgress.visibility = View.GONE
            val hasComments = comments.isNotEmpty()
            binding.rvComments.visibility = if (hasComments) View.VISIBLE else View.GONE
            binding.tvCommentsEmpty.visibility = if (hasComments) View.GONE else View.VISIBLE
            commentsAdapter.submitList(comments)
        }
    }

    private fun setupDots(container: LinearLayout, count: Int, activeIndex: Int) {
        container.removeAllViews()
        for (i in 0 until count) {
            val dot = View(container.context)
            val size = if (i == activeIndex) 10 else 8
            val dp = (size * container.context.resources.displayMetrics.density).toInt()
            val params = LinearLayout.LayoutParams(dp, dp)
            params.setMargins(4, 0, 4, 0)
            dot.layoutParams = params
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(
                if (i == activeIndex) 0xFFFFFFFF.toInt() else 0x80FFFFFF.toInt()
            )
            dot.background = drawable
            container.addView(dot)
        }
    }

    private fun updateDots(container: LinearLayout, activeIndex: Int) {
        for (i in 0 until container.childCount) {
            val dot = container.getChildAt(i)
            val isActive = i == activeIndex
            val size = if (isActive) 10 else 8
            val dp = (size * container.context.resources.displayMetrics.density).toInt()
            val params = dot.layoutParams as LinearLayout.LayoutParams
            params.width = dp
            params.height = dp
            dot.layoutParams = params
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(
                if (isActive) 0xFFFFFFFF.toInt() else 0x80FFFFFF.toInt()
            )
            dot.background = drawable
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        spotViewModel.stopObservingComments(args.spotId)
        _binding = null
    }
}
