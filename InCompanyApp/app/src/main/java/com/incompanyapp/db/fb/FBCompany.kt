package com.incompanyapp.db.fb

import com.google.android.gms.maps.model.LatLng
import com.incompanyapp.model.Clock
import com.incompanyapp.model.Company

class FBCompany {
    var name : String? = null
    var code : String? = null
    var lat : Double? = null
    var lng : Double? = null
    fun toCompany(): Company {
        val latlng = LatLng(lat ?: 0.0, lng ?: 0.0)
        return Company(name!!, code!!, location = latlng)
    }
}
fun Company.toFBCompany() : FBCompany {
    val fbCompany = FBCompany()
    fbCompany.name = this.name
    fbCompany.code = this.code
    fbCompany.lat = this.location?.latitude ?: 0.0
    fbCompany.lng = this.location?.longitude ?: 0.0
    return fbCompany
}
