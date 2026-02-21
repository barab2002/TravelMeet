package com.travelmeet.app.ui.profile

import android.app.Activity
import android.content.DialogInterface
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
    private var editProfileDialogBinding: DialogEditProfileBinding? = null

    private var commentDialog: androidx.appcompat.app.AlertDialog? = null
    private var commentInput: com.google.android.material.textfield.TextInputEditText? = null
    private var pendingCommentSpotId: String? = null

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPhotoUri = it
            editProfileDialogBinding?.ivDialogAvatar?.setImageURI(it)
        }
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
        binding.tvTotalLikes.text = getString(R.string.total_likes, 0)
        setupRecyclerView()
        setupClickListeners()
        loadUserProfile()
        observeUserSpots()
        observeProfileUpdate()
        observeCommentState()
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
            },
            onCommentClick = { spot ->
                showAddCommentDialog(spot)
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
        val user = authViewModel.currentUser
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
            when (resource) {
                is Resource.Loading -> {
                    binding.profileLoadSpinner.visibility = View.VISIBLE
                    binding.profileLoadSpinner.playAnimation()
                }
                is Resource.Success -> {
                    binding.profileLoadSpinner.cancelAnimation()
                    binding.profileLoadSpinner.visibility = View.GONE
                    resource.data?.let { userData ->
                        binding.tvUsername.text = userData.username
                        binding.tvEmail.text = userData.email
                        if (!userData.photoUrl.isNullOrEmpty()) {
                            Picasso.get().load(userData.photoUrl).into(binding.ivAvatar)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.profileLoadSpinner.cancelAnimation()
                    binding.profileLoadSpinner.visibility = View.GONE
                }
            }
        }
    }

    private fun observeUserSpots() {
        val userId = authViewModel.currentUserId ?: return
        spotViewModel.getSpotsByUser(userId).observe(viewLifecycleOwner) { spots ->
            mySpotsAdapter.submitList(spots)
            binding.tvMySpots.text = getString(R.string.my_spots, spots.size)
            val totalLikes = spots.sumOf { it.likesCount }
            binding.tvTotalLikes.text = getString(R.string.total_likes, totalLikes)
        }
    }

    private fun showEditProfileDialog() {
        selectedPhotoUri = null
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater).also {
            editProfileDialogBinding = it
        }
        val currentUser = authViewModel.currentUser

        dialogBinding.etUsername.setText(currentUser?.displayName ?: "")
        if (currentUser?.photoUrl != null) {
            Picasso.get().load(currentUser.photoUrl).into(dialogBinding.ivDialogAvatar)
        } else {
            dialogBinding.ivDialogAvatar.setImageResource(R.drawable.ic_profile)
        }

        dialogBinding.btnChangePhoto.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.update_profile, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val newUsername = dialogBinding.etUsername.text.toString().trim()
                if (newUsername.isEmpty()) {
                    dialogBinding.tilUsername.error = getString(R.string.username)
                    return@setOnClickListener
                }
                dialogBinding.tilUsername.error = null
                authViewModel.updateProfile(newUsername, selectedPhotoUri)
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            editProfileDialogBinding = null
            selectedPhotoUri = null
        }

        dialog.show()
    }

    private fun observeProfileUpdate() {
        authViewModel.profileUpdateState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(getString(R.string.updating_profile))
                }
                is Resource.Success -> {
                    hideLoading()
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    loadUserProfile()
                }
                is Resource.Error -> {
                    hideLoading()
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeCommentState() {
        spotViewModel.commentState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            when (state) {
                is Resource.Loading -> {
                    commentInput?.isEnabled = false
                    commentDialog?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)?.isEnabled = false
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
                    commentDialog?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)?.isEnabled = true
                    Toast.makeText(requireContext(), state.message ?: getString(R.string.error), Toast.LENGTH_LONG).show()
                    spotViewModel.resetCommentState()
                }
            }
        }
    }

    private fun showAddCommentDialog(spot: com.travelmeet.app.data.local.entity.SpotEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        val input = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_comment)
        val til = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_comment)
        val btnSubmit = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancel)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSubmit.setOnClickListener {
            val text = input.text?.toString()?.trim().orEmpty()
            if (text.length < 2) {
                til.error = getString(R.string.add_comment_hint)
                return@setOnClickListener
            }
            til.error = null
            pendingCommentSpotId = spot.id
            commentInput = input
            btnSubmit.isEnabled = false
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

    private fun showLoading(message: String) {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.profileProgressBar.playAnimation()
        binding.tvLoadingMessage.text = message
    }

    private fun hideLoading() {
        binding.profileProgressBar.cancelAnimation()
        binding.loadingOverlay.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editProfileDialogBinding = null
        _binding = null
    }
}
