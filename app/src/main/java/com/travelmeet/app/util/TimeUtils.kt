package com.travelmeet.app.util

import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "${minutes}m ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "${hours}h ago"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "${days}d ago"
            }
            diff < TimeUnit.DAYS.toMillis(30) -> {
                val weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7
                "${weeks}w ago"
            }
            else -> {
                val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
                "${months}mo ago"
            }
        }
    }
}
