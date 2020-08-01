package com.umair.mapbox_navigation.mapbox

import android.annotation.SuppressLint
import android.content.Context
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

object MapUtils {

    private var destination: Point? = null
    private var waypoint: Point? = null
    private var locationEngine: LocationEngine? = null
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    private fun addDestination(point: LatLng) {
        if (destination == null) {
            destination = Point.fromLngLat(point.longitude, point.latitude)
        } else if (waypoint == null) {
            waypoint = Point.fromLngLat(point.longitude, point.latitude)
        }
    }


    @SuppressLint("MissingPermission")
    private fun calculateRoute(context: Context) {
        locationEngine?.getLastLocation(object : LocationEngineCallback<LocationEngineResult> {
            override fun onSuccess(result: LocationEngineResult?) {
                findRouteWith(result, context)
            }

            override fun onFailure(exception: Exception) {
                Timber.e(exception)
            }
        })
    }


    fun findRouteWith(result: LocationEngineResult?, context: Context) {
        result?.let {
            val userLocation = result.lastLocation
            if (userLocation == null) {
                Timber.d("calculateRoute: User location is null, therefore, origin can't be set.")
                return
            }
            destination?.let { destination ->
                val origin = Point.fromLngLat(userLocation.longitude, userLocation.latitude)
                if (TurfMeasurement.distance(origin, destination, TurfConstants.UNIT_METERS) < 50) {
                    return
                }
                val navigationRouteBuilder = NavigationRoute.builder(context)
                        .accessToken(Mapbox.getAccessToken()!!)
                navigationRouteBuilder.origin(origin)
                navigationRouteBuilder.destination(destination)
                if (waypoint != null) {
                    navigationRouteBuilder.addWaypoint(waypoint!!)
                }
                navigationRouteBuilder.enableRefresh(true)

                navigationRouteBuilder.build().getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>) {
                        Timber.d("Url: %s", call.request().url().toString())
                        if (response.body() != null) {
                            if (response.body()!!.routes().isNotEmpty()) {
                                currentRoute = response.body()!!.routes()[0]
                                navigationMapRoute?.addRoutes(response.body()!!.routes())
                            }
                        }
                    }

                    override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                        Timber.e(throwable, "onFailure: navigation.getRoute()")
                    }
                })
            }
        }
    }

    fun computeHeading(from: LatLng, to: LatLng): Double {
        // Compute bearing/heading using Turf and return the value.
        return TurfMeasurement.bearing(
                Point.fromLngLat(from.latitude, from.longitude),
                Point.fromLngLat(to.latitude, to.longitude)
        )
    }
}