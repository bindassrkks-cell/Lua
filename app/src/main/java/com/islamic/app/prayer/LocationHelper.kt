package com.islamic.app.prayer

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class UserLocation(val city: String, val latitude: Double, val longitude: Double)

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getDeviceLocation(context: Context): UserLocation = withContext(Dispatchers.IO) {
        var bestLoc: Location? = null
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = lm.getProviders(true)
            for (p in providers) {
                val l = lm.getLastKnownLocation(p) ?: continue
                if (bestLoc == null || l.accuracy < bestLoc!!.accuracy) {
                    bestLoc = l
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val lat = bestLoc?.latitude ?: 51.5074
        val lng = bestLoc?.longitude ?: -0.1278
        var cityName = if (bestLoc != null) "Detected Location" else "London"

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocation(lat, lng, 1)
            if (!list.isNullOrEmpty()) {
                cityName = list[0].locality ?: list[0].subAdminArea ?: list[0].adminArea ?: "Local City"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        UserLocation(city = cityName, latitude = lat, longitude = lng)
    }
}
