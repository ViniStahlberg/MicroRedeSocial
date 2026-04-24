package com.example.microredesocial.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onFailure(SecurityException("Permissão de localização negada"))
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location)
                } else {
                    onFailure(Exception("Localização não disponível"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCityFromLocation(location: Location): String {
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (!addresses.isNullOrEmpty()) {
                addresses[0].subAdminArea ?: addresses[0].locality ?: "Cidade não identificada"
            } else {
                "Cidade não identificada"
            }
        } catch (e: Exception) {
            "Cidade não identificada"
        }
    }
}