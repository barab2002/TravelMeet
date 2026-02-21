package com.travelmeet.app.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.travelmeet.app.R
import com.travelmeet.app.databinding.FragmentFeedBinding
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.util.Resource

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val spotViewModel: SpotViewModel by activityViewModels()
    private lateinit var spotAdapter: SpotAdapter

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
        setupRecyclerView()
        setupSwipeRefresh()
        setupToolbar()
        observeSpots()

        // Initial sync
        spotViewModel.syncSpots()
    }

    private fun setupRecyclerView() {
        spotAdapter = SpotAdapter(
            onItemClick = { spot ->
                val action = FeedFragmentDirections.actionFeedFragmentToSpotDetailFragment(spot.id)
                findNavController().navigate(action)
            },
            onLikeClick = { spot ->
                spotViewModel.toggleLike(spot.id)
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
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_feed)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_sort -> {
                    showSortFilterSheet()
                    true
                }
                else -> false
            }
        }
    }

    // New nicer UI: bottom sheet with sort + search + location placeholders
    private fun showSortFilterSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_sort_filter, null)

        val sortOption = spotViewModel.sortOption.value ?: SpotSortOption.DEFAULT

        val chipGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.sortChipGroup)
        chipGroup.removeAllViews()
        SpotSortOption.OPTIONS.forEach { option ->
            val chip = layoutInflater.inflate(R.layout.item_filter_chip, chipGroup, false) as com.google.android.material.chip.Chip
            chip.text = getString(option.labelRes)
            chip.isCheckable = true
            chip.isChecked = option == sortOption
            chip.setOnClickListener {
                for (i in 0 until chipGroup.childCount) {
                    val childChip = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                    childChip.isChecked = childChip == chip
                }
            }
            chipGroup.addView(chip)
        }

        val searchInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.searchInput)
        val locationInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.locationInput)

        val clearButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonClear)
        val applyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonApply)

        clearButton.setOnClickListener {
            searchInput.text?.clear()
            locationInput.text?.clear()
            // Reset selection to default sort
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? com.google.android.material.chip.Chip ?: continue
                chip.isChecked = chip.text == getString(SpotSortOption.DEFAULT.labelRes)
            }
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

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
