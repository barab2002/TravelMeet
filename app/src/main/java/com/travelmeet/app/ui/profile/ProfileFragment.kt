package com.travelmeet.app.ui.profile

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.databinding.DialogEditProfileBinding
import com.travelmeet.app.databinding.FragmentProfileBinding
import com.travelmeet.app.ui.feed.SpotAdapter
import com.travelmeet.app.ui.viewmodel.AuthViewModel
import com.travelmeet.app.ui.viewmodel.SpotViewModel
import com.travelmeet.app.util.Resource

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val spotViewModel: SpotViewModel by activityViewModels()
    private lateinit var mySpotsAdapter: SpotAdapter
    private var selectedPhotoUri: Uri? = null

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedPhotoUri = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        loadUserProfile()
        observeUserSpots()
        observeProfileUpdate()
    }

    private fun setupRecyclerView() {
        mySpotsAdapter = SpotAdapter(
            onItemClick = { spot ->
                val action = ProfileFragmentDirections
                    .actionProfileFragmentToSpotDetailFragment(spot.id)
                findNavController().navigate(action)
            },
            onLikeClick = { spot ->
                spotViewModel.toggleLike(spot.id)
            }
        )
        binding.rvMySpots.apply {
            adapter = mySpotsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.cardEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.cardMySpots.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mySpotsFragment)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton(R.string.logout) { _, _ ->
                    authViewModel.logout()
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            binding.tvUsername.text = it.displayName ?: "User"
            binding.tvEmail.text = it.email ?: ""

            if (it.photoUrl != null) {
                Picasso.get().load(it.photoUrl).into(binding.ivAvatar)
            }

            // Load additional user data
            authViewModel.loadUserData(it.uid)
        }

        authViewModel.userData.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { userData ->
                    binding.tvUsername.text = userData.username
                    binding.tvEmail.text = userData.email
                    if (!userData.photoUrl.isNullOrEmpty()) {
                        Picasso.get().load(userData.photoUrl).into(binding.ivAvatar)
                    }
                }
            }
        }
    }

    private fun observeUserSpots() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        spotViewModel.getSpotsByUser(userId).observe(viewLifecycleOwner) { spots ->
            mySpotsAdapter.submitList(spots)
            binding.tvMySpots.text = getString(R.string.my_spots, spots.size)
        }
    }

    private fun showEditProfileDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        val currentUser = FirebaseAuth.getInstance().currentUser

        dialogBinding.etUsername.setText(currentUser?.displayName ?: "")
        if (currentUser?.photoUrl != null) {
            Picasso.get().load(currentUser.photoUrl).into(dialogBinding.ivDialogAvatar)
        }

        dialogBinding.btnChangePhoto.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.update_profile) { _, _ ->
                val newUsername = dialogBinding.etUsername.text.toString().trim()
                if (newUsername.isNotEmpty()) {
                    authViewModel.updateProfile(newUsername, selectedPhotoUri)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeProfileUpdate() {
        authViewModel.profileUpdateState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    loadUserProfile()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
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
