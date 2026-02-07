package com.travelmeet.app.ui.profile

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.travelmeet.app.R
import kotlin.Int
import kotlin.String

public class ProfileFragmentDirections private constructor() {
  private data class ActionProfileFragmentToSpotDetailFragment(
    public val spotId: String
  ) : NavDirections {
    public override val actionId: Int = R.id.action_profileFragment_to_spotDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("spotId", this.spotId)
        return result
      }
  }

  public companion object {
    public fun actionProfileFragmentToLoginFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_loginFragment)

    public fun actionProfileFragmentToSpotDetailFragment(spotId: String): NavDirections =
        ActionProfileFragmentToSpotDetailFragment(spotId)

    public fun actionProfileFragmentToMySpotsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_mySpotsFragment)
  }
}
