package com.incompanyapp.model

import com.google.android.gms.maps.model.LatLng

data class Company(
    val name: String,
    val code: String,
    var location: LatLng? = null
)
