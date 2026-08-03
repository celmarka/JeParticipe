package com.citoyen.jeparticipe.data.location


import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class LocationManager(
    private val context: Context
) {


    private val client =
        LocationServices
            .getFusedLocationProviderClient(context)



    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation()
            : Pair<Double, Double>? {


        return suspendCancellableCoroutine { continuation ->


            client.lastLocation

                .addOnSuccessListener { location ->


                    if(location != null){


                        continuation.resume(

                            Pair(

                                location.latitude,

                                location.longitude

                            )

                        )


                    }else{


                        continuation.resume(null)

                    }


                }

        }

    }

}