package com.travelmeet.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.travelmeet.app.R
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
    private var commentDialog: androidx.appcompat.app.AlertDialog? = null
    private var commentInput: com.google.android.material.textfield.TextInputEditText? = null
    private var pendingCommentSpotId: String? = null

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
        observeCommentState()
    }

    private fun setupRecyclerView() {
        spotAdapter = SpotAdapter(
            onItemClick = { spot ->
                val action = MySpotsFragmentDirections.actionMySpotsFragmentToSpotDetailFragment(spot.id)
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

    private fun observeCommentState() {
        spotViewModel.commentState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            when (state) {
                is com.travelmeet.app.util.Resource.Loading -> {
                    commentInput?.isEnabled = false
                    commentDialog?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit)?.isEnabled = false
                }
                is com.travelmeet.app.util.Resource.Success -> {
                    Toast.makeText(requireContext(), R.string.comments, Toast.LENGTH_SHORT).show()
                    commentDialog?.dismiss()
                    commentDialog = null
                    commentInput = null
                    pendingCommentSpotId = null
                    spotViewModel.resetCommentState()
                }
                is com.travelmeet.app.util.Resource.Error -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        commentDialog = null
        commentInput = null
        pendingCommentSpotId = null
        _binding = null
    }
}
