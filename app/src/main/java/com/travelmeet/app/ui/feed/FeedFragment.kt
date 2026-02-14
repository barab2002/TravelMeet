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
        // Observe cached spots from Room
        spotViewModel.allSpots.observe(viewLifecycleOwner) { spots ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
