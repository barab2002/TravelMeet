package com.travelmeet.app.ui.feed

import androidx.annotation.StringRes
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity

enum class SpotSortField {
    CREATED_DATE
}

enum class SortDirection {
    ASCENDING,
    DESCENDING
}

enum class SpotSortOption(
    val field: SpotSortField,
    val direction: SortDirection,
    @StringRes val labelRes: Int
) {
    CREATED_DATE_DESCENDING(SpotSortField.CREATED_DATE, SortDirection.DESCENDING, R.string.sort_newest),
    CREATED_DATE_ASCENDING(SpotSortField.CREATED_DATE, SortDirection.ASCENDING, R.string.sort_oldest);

    companion object {
        val DEFAULT = CREATED_DATE_DESCENDING

        val OPTIONS: List<SpotSortOption> = values().toList()
    }
}

fun SpotSortOption.sort(spots: List<SpotEntity>): List<SpotEntity> {
    val comparator = when (field) {
        SpotSortField.CREATED_DATE -> compareBy<SpotEntity> { it.timestamp }
    }
    return when (direction) {
        SortDirection.ASCENDING -> spots.sortedWith(comparator)
        SortDirection.DESCENDING -> spots.sortedWith(comparator.reversed())
    }
}
