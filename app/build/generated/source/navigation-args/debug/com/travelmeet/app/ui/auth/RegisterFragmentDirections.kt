package com.travelmeet.app.ui.auth

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.travelmeet.app.R

public class RegisterFragmentDirections private constructor() {
  public companion object {
    public fun actionRegisterFragmentToFeedFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_registerFragment_to_feedFragment)
  }
}
