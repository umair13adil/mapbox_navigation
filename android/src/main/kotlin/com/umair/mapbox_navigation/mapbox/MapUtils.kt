package com.umair.mapbox_navigation.mapbox

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
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
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.umair.mapbox_navigation.R
import com.umair.mapbox_navigation.models.EventSendHelper
import com.umair.mapbox_navigation.models.MapBoxEvents
import com.umair.mapbox_navigation.models.ProgressData
import com.umair.mapbox_navigation.plugin.FlutterMapViewFactory
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
    
    fun doOnProgressChange(location: Location, routeProgress: RouteProgress){
        val upComingStepBearingAfter = routeProgress.currentLegProgress()?.upComingStep?.maneuver()?.bearingAfter()
        val upComingStepBearingBefore = routeProgress.currentLegProgress()?.upComingStep?.maneuver()?.bearingBefore()
        val currentStepBearingAfter = routeProgress.currentLegProgress()?.currentStep?.maneuver()?.bearingAfter()
        val currentStepBearingBefore = routeProgress.currentLegProgress()?.currentStep?.maneuver()?.bearingBefore()

        EventSendHelper.sendEvent(MapBoxEvents.PROGRESS_CHANGE,
                ProgressData(
                        currentLatitude = location.latitude,
                        currentLongitude = location.longitude,
                        upcomingLatitude = routeProgress.upcomingStepPoints()?.first()?.latitude(),
                        upcomingLongitude = routeProgress.upcomingStepPoints()?.first()?.longitude(),
                        distanceTraveled = routeProgress.distanceTraveled(),
                        legDistanceTraveled = routeProgress.currentLegProgress?.distanceTraveled,
                        legDistanceRemaining = routeProgress.legDistanceRemaining,
                        legDurationRemaining = routeProgress.legDurationRemaining,
                        voiceInstruction = routeProgress.voiceInstruction?.announcement,
                        bannerInstruction = routeProgress.bannerInstruction?.primary?.text,
                        legIndex = routeProgress.legIndex,
                        stepIndex = routeProgress.stepIndex,
                        currentStepBearingAfter = currentStepBearingAfter,
                        currentStepBearingBefore = currentStepBearingBefore,
                        currentStepDrivingSide = routeProgress.currentLegProgress()?.currentStep()?.drivingSide(),
                        currentStepExits = routeProgress.currentLegProgress()?.currentStep()?.exits(),
                        currentStepDistance = routeProgress.currentLegProgress()?.currentStep()?.distance(),
                        currentStepDuration = routeProgress.currentLegProgress()?.currentStep()?.duration(),
                        currentStepName = routeProgress.currentLegProgress()?.currentStep()?.name(),
                        upComingStepBearingAfter = upComingStepBearingAfter,
                        upComingStepBearingBefore = upComingStepBearingBefore,
                        upComingStepDrivingSide = routeProgress.currentLegProgress()?.upComingStep()?.drivingSide(),
                        upComingStepExits = routeProgress.currentLegProgress()?.upComingStep()?.exits(),
                        upComingStepDistance = routeProgress.currentLegProgress()?.upComingStep()?.distance(),
                        upComingStepDuration = routeProgress.currentLegProgress()?.upComingStep()?.duration(),
                        upComingStepName = routeProgress.currentLegProgress()?.upComingStep()?.name()
                ).toString())

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onProgressChange, %s, %s", "Current Location: ${location.latitude},${location.longitude}",
                    "Distance Remaining: ${routeProgress.currentLegProgress?.distanceRemaining}"))
    }
}