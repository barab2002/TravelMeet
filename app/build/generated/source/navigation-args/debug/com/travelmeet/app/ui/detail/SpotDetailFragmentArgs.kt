package com.travelmeet.app.ui.detail

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class SpotDetailFragmentArgs(
  public val spotId: String
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("spotId", this.spotId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("spotId", this.spotId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): SpotDetailFragmentArgs {
      bundle.setClassLoader(SpotDetailFragmentArgs::class.java.classLoader)
      val __spotId : String?
      if (bundle.containsKey("spotId")) {
        __spotId = bundle.getString("spotId")
        if (__spotId == null) {
          throw IllegalArgumentException("Argument \"spotId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"spotId\" is missing and does not have an android:defaultValue")
      }
      return SpotDetailFragmentArgs(__spotId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): SpotDetailFragmentArgs {
      val __spotId : String?
      if (savedStateHandle.contains("spotId")) {
        __spotId = savedStateHandle["spotId"]
        if (__spotId == null) {
          throw IllegalArgumentException("Argument \"spotId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"spotId\" is missing and does not have an android:defaultValue")
      }
      return SpotDetailFragmentArgs(__spotId)
    }
  }
}
