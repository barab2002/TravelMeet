package com.travelmeet.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelmeet.app.databinding.FragmentMySpotsBinding
import com.travelmeet.app.ui.feed.SpotAdapter
import com.travelmeet.app.ui.viewmodel.AuthViewModel
import com.travelmeet.app.ui.viewmodel.SpotViewModel

class MySpotsFragment : Fragment() {

    private var _binding: FragmentMySpotsBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val spotViewModel: SpotViewModel by activityViewModels()
    private lateinit var spotAdapter: SpotAdapter

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
        binding.rvMySpots.apply {
            adapter = spotAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeMySpots() {
        val userId = authViewModel.currentUserId
        if (userId != null) {
            spotViewModel.getSpotsByUser(userId).observe(viewLifecycleOwner) { spots ->
                binding.mySpotsProgressBar.cancelAnimation()
                binding.mySpotsProgressBar.visibility = View.GONE
                spotAdapter.submitList(spots)
                binding.emptyState.visibility = if (spots.isEmpty()) View.VISIBLE else View.GONE
                binding.rvMySpots.visibility = if (spots.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
