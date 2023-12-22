package com.goldenKids.dotami
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(private val activity: AppCompatActivity) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    // 위치 권한 체크 및 요청
    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
            return false
        }
        return true
    }

    // 위치 정보 요청 및 반환
    fun requestLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        callback(latitude, longitude)
                    }
                }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 123
    }
}
