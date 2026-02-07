package com.travelmeet.app.ui.auth

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.travelmeet.app.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginFragmentToFeedFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_feedFragment)

    public fun actionLoginFragmentToRegisterFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_registerFragment)
  }
}
