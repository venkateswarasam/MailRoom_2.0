package com.xcarriermaterialdesign.dbmodel

import org.json.JSONArray

data class CountriesItem(
    val alpha2Code: String,
    val alpha3Code: String,
    val country: String,
    val numberCode: String,
    val states: JSONArray
)