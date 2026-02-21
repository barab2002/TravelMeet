package com.travelmeet.app.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.travelmeet.app.R
import com.travelmeet.app.databinding.BottomSheetSortFilterBinding

// This file is no longer used. The feed sort/filter UI is now implemented
// directly via a BottomSheetDialog in FeedFragment to avoid DialogFragment
// attachment issues.

class SortFilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSortFilterBinding? = null
    private val binding get() = _binding!!

    private var selectedSort: SpotSortOption = SpotSortOption.DEFAULT

    var onApply: ((SpotSortOption) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedSort = arguments
            ?.getString(ARG_SORT_KEY)
            ?.let { SpotSortOption.valueOf(it) }
            ?: SpotSortOption.DEFAULT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSortFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSortChips()

        binding.buttonApply.setOnClickListener {
            onApply?.invoke(selectedSort)
            dismiss()
        }

        binding.buttonClear.setOnClickListener {
            selectedSort = SpotSortOption.DEFAULT
            updateChipSelection()
        }
    }

    private fun setupSortChips() {
        val chipGroup = binding.sortChipGroup
        chipGroup.removeAllViews()
        SpotSortOption.OPTIONS.forEach { option ->
            val chip = layoutInflater.inflate(
                R.layout.item_filter_chip,
                chipGroup,
                false
            ) as Chip
            chip.text = getString(option.labelRes)
            chip.isCheckable = true
            chip.isChecked = option == selectedSort
            chip.setOnClickListener {
                selectedSort = option
                updateChipSelection()
            }
            chipGroup.addView(chip)
        }
    }

    private fun updateChipSelection() {
        for (i in 0 until binding.sortChipGroup.childCount) {
            val chip = binding.sortChipGroup.getChildAt(i) as? Chip ?: continue
            chip.isChecked = chip.text == getString(selectedSort.labelRes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SortFilterBottomSheet"
        private const val ARG_SORT_KEY = "arg_sort"

        fun newInstance(selected: SpotSortOption): SortFilterBottomSheet {
            return SortFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_SORT_KEY, selected.name)
                }
            }
        }
    }
}
