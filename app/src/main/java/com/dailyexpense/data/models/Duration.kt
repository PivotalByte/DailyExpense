package com.dailyexpense.data.models

sealed class Duration {
    object Today : Duration()
    object ThisWeek : Duration()
    object ThisMonth : Duration()
    object ThisYear : Duration()
    object Custom : Duration()
}
