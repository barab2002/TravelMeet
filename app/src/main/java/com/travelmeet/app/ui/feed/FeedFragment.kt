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
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.FragmentFeedBinding
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.util.Resource
import kotlin.math.min

class FeedFragment : Fragment() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val spotViewModel: SpotViewModel by activityViewModels()
    private lateinit var spotAdapter: SpotAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager
    private var pagedSpots: List<SpotEntity> = emptyList()
    private var currentPage = 0

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
        feedLayoutManager = LinearLayoutManager(requireContext())
        binding.rvSpots.apply {
            adapter = spotAdapter
            layoutManager = feedLayoutManager
        }
        setupPaginationControls()
    }

    private fun setupPaginationControls() {
        binding.btnNextPage.setOnClickListener {
            if (canGoNext()) {
                currentPage++
                submitFeedPage()
                binding.rvSpots.scrollToPosition(0)
            }
        }
        binding.btnPrevPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                submitFeedPage()
                binding.rvSpots.scrollToPosition(0)
            }
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
        spotViewModel.allSpots.observe(viewLifecycleOwner) { spots ->
            pagedSpots = spots
            currentPage = 0
            val isEmpty = spots.isEmpty()
            binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.rvSpots.visibility = if (isEmpty) View.GONE else View.VISIBLE
            if (isEmpty) {
                spotAdapter.submitList(emptyList())
                updatePaginationUi(0)
            } else {
                submitFeedPage()
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

    private fun canGoNext(): Boolean {
        val pages = totalPages()
        return pages > 0 && currentPage < pages - 1
    }

    private fun submitFeedPage() {
        val pages = totalPages()
        if (pages == 0) {
            spotAdapter.submitList(emptyList())
            updatePaginationUi(pages)
            return
        }
        if (currentPage >= pages) currentPage = pages - 1
        val startIndex = currentPage * PAGE_SIZE
        val endIndex = min(startIndex + PAGE_SIZE, pagedSpots.size)
        val subList = pagedSpots.subList(startIndex, endIndex)
        spotAdapter.submitList(ArrayList(subList))
        updatePaginationUi(pages)
    }

    private fun totalPages(): Int =
        if (pagedSpots.isEmpty()) 0 else ((pagedSpots.size - 1) / PAGE_SIZE) + 1

    private fun updatePaginationUi(pages: Int) {
        val showPagination = pagedSpots.size > PAGE_SIZE
        binding.paginationContainer.visibility = if (showPagination) View.VISIBLE else View.GONE
        binding.btnPrevPage.isEnabled = showPagination && currentPage > 0
        binding.btnNextPage.isEnabled = showPagination && currentPage < pages - 1
        binding.tvPageIndicator.text = if (pages == 0) {
            getString(R.string.page_indicator, 0, 0)
        } else {
            getString(R.string.page_indicator, currentPage + 1, pages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
