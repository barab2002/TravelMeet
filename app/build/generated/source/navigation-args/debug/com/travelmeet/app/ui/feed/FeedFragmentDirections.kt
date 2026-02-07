package com.travelmeet.app.ui.feed

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.travelmeet.app.R
import kotlin.Int
import kotlin.String

public class FeedFragmentDirections private constructor() {
  private data class ActionFeedFragmentToSpotDetailFragment(
    public val spotId: String
  ) : NavDirections {
    public override val actionId: Int = R.id.action_feedFragment_to_spotDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("spotId", this.spotId)
        return result
      }
  }

  public companion object {
    public fun actionFeedFragmentToSpotDetailFragment(spotId: String): NavDirections =
        ActionFeedFragmentToSpotDetailFragment(spotId)

    public fun actionFeedFragmentToAddSpotFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_feedFragment_to_addSpotFragment)
  }
}
