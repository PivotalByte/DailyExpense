package com.dailyexpense.data.models

import com.dailyexpense.data.enums.SortOption
import com.dailyexpense.data.enums.SortOrder

data class SortState(
    var sortOption: SortOption = SortOption.DATE,
    var sortOrder: SortOrder = SortOrder.DESCENDING

)
