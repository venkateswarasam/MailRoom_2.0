package com.xcarriermaterialdesign.activities.country

data class Country(
    val alpha2Code: String,
    val alpha3Code: String,
    val country: String,
    val numberCode: String,
    val states: List<String>
)