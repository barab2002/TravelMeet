package com.travelmeet.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.FragmentMySpotsBinding
import com.travelmeet.app.ui.feed.SpotAdapter
import com.travelmeet.app.ui.viewmodel.AuthViewModel
import com.travelmeet.app.ui.viewmodel.SpotViewModel

class MySpotsFragment : Fragment() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    private var _binding: FragmentMySpotsBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val spotViewModel: SpotViewModel by activityViewModels()
    private lateinit var spotAdapter: SpotAdapter
    private lateinit var spotsLayoutManager: LinearLayoutManager
    private var pagedSpots: List<SpotEntity> = emptyList()
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySpotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMySpots()
    }

    private fun setupRecyclerView() {
        spotAdapter = SpotAdapter(
            onItemClick = { spot ->
                val action = MySpotsFragmentDirections.actionMySpotsFragmentToSpotDetailFragment(spot.id)
                findNavController().navigate(action)
            },
            onLikeClick = { spot ->
                spotViewModel.toggleLike(spot.id)
            }
        )
        spotsLayoutManager = LinearLayoutManager(requireContext())
        binding.rvMySpots.apply {
            adapter = spotAdapter
            layoutManager = spotsLayoutManager
        }
        setupPaginationControls()
    }

    private fun setupPaginationControls() {
        binding.btnNextPage.setOnClickListener {
            if (canGoNext()) {
                currentPage++
                submitMySpotsPage()
                binding.rvMySpots.scrollToPosition(0)
            }
        }
        binding.btnPrevPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                submitMySpotsPage()
                binding.rvMySpots.scrollToPosition(0)
            }
        }
    }

    private fun canGoNext(): Boolean {
        val pages = totalPages()
        return pages > 0 && currentPage < pages - 1
    }

    private fun submitMySpotsPage() {
        val pages = totalPages()
        if (pages == 0) {
            spotAdapter.submitList(emptyList())
            updatePaginationUi(pages)
            return
        }
        if (currentPage >= pages) currentPage = pages - 1
        val start = currentPage * PAGE_SIZE
        val end = minOf(start + PAGE_SIZE, pagedSpots.size)
        val newSpots = pagedSpots.subList(start, end)
        spotAdapter.submitList(ArrayList(newSpots))
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

    private fun observeMySpots() {
        val userId = authViewModel.currentUserId
        if (userId != null) {
            spotViewModel.getSpotsByUser(userId).observe(viewLifecycleOwner) { spots ->
                pagedSpots = spots
                currentPage = 0
                val isEmpty = spots.isEmpty()
                binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.rvMySpots.visibility = if (isEmpty) View.GONE else View.VISIBLE
                if (isEmpty) {
                    spotAdapter.submitList(emptyList())
                    updatePaginationUi(0)
                } else {
                    submitMySpotsPage()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
