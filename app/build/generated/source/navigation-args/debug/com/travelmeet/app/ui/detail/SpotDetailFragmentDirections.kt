package com.travelmeet.app.ui.detail

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.travelmeet.app.R

public class SpotDetailFragmentDirections private constructor() {
  public companion object {
    public fun actionSpotDetailFragmentToAddSpotFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_spotDetailFragment_to_addSpotFragment)
  }
}
