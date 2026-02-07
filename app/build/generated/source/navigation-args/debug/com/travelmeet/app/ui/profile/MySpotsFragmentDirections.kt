package com.travelmeet.app.ui.profile

import android.os.Bundle
import androidx.navigation.NavDirections
import com.travelmeet.app.R
import kotlin.Int
import kotlin.String

public class MySpotsFragmentDirections private constructor() {
  private data class ActionMySpotsFragmentToSpotDetailFragment(
    public val spotId: String
  ) : NavDirections {
    public override val actionId: Int = R.id.action_mySpotsFragment_to_spotDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("spotId", this.spotId)
        return result
      }
  }

  public companion object {
    public fun actionMySpotsFragmentToSpotDetailFragment(spotId: String): NavDirections =
        ActionMySpotsFragmentToSpotDetailFragment(spotId)
  }
}
