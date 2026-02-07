package com.travelmeet.app.ui.map

import android.os.Bundle
import androidx.navigation.NavDirections
import com.travelmeet.app.R
import kotlin.Int
import kotlin.String

public class MapFragmentDirections private constructor() {
  private data class ActionMapFragmentToSpotDetailFragment(
    public val spotId: String
  ) : NavDirections {
    public override val actionId: Int = R.id.action_mapFragment_to_spotDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("spotId", this.spotId)
        return result
      }
  }

  public companion object {
    public fun actionMapFragmentToSpotDetailFragment(spotId: String): NavDirections =
        ActionMapFragmentToSpotDetailFragment(spotId)
  }
}
