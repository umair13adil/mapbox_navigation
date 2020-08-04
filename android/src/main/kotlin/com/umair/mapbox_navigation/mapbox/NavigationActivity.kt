package com.umair.mapbox_navigation.mapbox

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import com.mapbox.services.android.navigation.v5.milestone.Milestone
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener
import com.mapbox.services.android.navigation.v5.route.FasterRouteListener
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.umair.mapbox_navigation.R
import com.umair.mapbox_navigation.models.*
import com.umair.mapbox_navigation.plugin.FlutterMapViewFactory
import kotlinx.android.synthetic.main.activity_navigation.*
import timber.log.Timber

class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback,
        ProgressChangeListener,
        OffRouteListener,
        MilestoneEventListener,
        NavigationEventListener,
        NavigationListener,
        FasterRouteListener,
        SpeechAnnouncementListener,
        BannerInstructionsListener,
        RouteListener {

    private lateinit var navigationMapboxMap: NavigationMapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private val route by lazy { getDirectionsRoute() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "")

        setContentView(R.layout.activity_navigation)

        navigationView.onCreate(savedInstanceState)
        navigationView.initialize(
                this,
                getInitialCameraPosition()
        )
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        navigationView.onResume()
    }

    override fun onStop() {
        super.onStop()
        navigationView.onStop()
    }

    override fun onPause() {
        super.onPause()
        navigationView.onPause()
    }

    override fun onDestroy() {
        navigationView.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView.onRestoreInstanceState(savedInstanceState)
    }

    override fun onNavigationReady(isRunning: Boolean) {
        if (!isRunning && !::navigationMapboxMap.isInitialized) {
            navigationView.retrieveNavigationMapboxMap()?.let { navMapboxMap ->
                this.navigationMapboxMap = navMapboxMap
                this.navigationMapboxMap.updateLocationLayerRenderMode(RenderMode.NORMAL)
                navigationView.retrieveMapboxNavigation()?.let {
                    this.mapboxNavigation = it

                    mapboxNavigation.addOffRouteListener(this)
                    mapboxNavigation.addFasterRouteListener(this)
                    mapboxNavigation.addNavigationEventListener(this)
                }

                val optionsBuilder = NavigationViewOptions.builder()
                optionsBuilder.progressChangeListener(this)
                optionsBuilder.milestoneEventListener(this)
                optionsBuilder.navigationListener(this)
                optionsBuilder.speechAnnouncementListener(this)
                optionsBuilder.bannerInstructionsListener(this)
                optionsBuilder.routeListener(this)
                optionsBuilder.directionsRoute(route)
                optionsBuilder.shouldSimulateRoute(true)

                navigationView.startNavigation(optionsBuilder.build())

            }
        }
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        MapUtils.doOnProgressChange(location, routeProgress, this)
    }

    override fun userOffRoute(location: Location) {

        EventSendHelper.sendEvent(MapBoxEvents.USER_OFF_ROUTE,
                LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude
                ).toString())

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("userOffRoute, %s", "Current Location: ${location.latitude},${location.longitude}"))
    }

    override fun onMilestoneEvent(routeProgress: RouteProgress, instruction: String, milestone: Milestone) {

        EventSendHelper.sendEvent(MapBoxEvents.MILESTONE_EVENT,
                MileStoneData(
                        identifier = milestone.identifier,
                        distanceTraveled = routeProgress.distanceTraveled(),
                        legIndex = routeProgress.legIndex,
                        stepIndex = routeProgress.stepIndex
                ).toString())

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onMilestoneEvent, %s, %s, %s",
                    "Distance Remaining: ${routeProgress.currentLegProgress?.distanceRemaining}",
                    "Instruction: $instruction",
                    "Milestone: ${milestone.instruction}"
            ))
    }

    override fun onRunning(running: Boolean) {

        EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_RUNNING)

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onRunning, %s", "$running"))
    }

    override fun onCancelNavigation() {
        EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_CANCELLED)

        navigationView.stopNavigation()
        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onCancelNavigation, %s", ""))
    }

    override fun onNavigationFinished() {
        EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_FINISHED)

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onNavigationFinished, %s", ""))
    }

    override fun onNavigationRunning() {
        EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_RUNNING)

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onNavigationRunning, %s", ""))
    }

    override fun fasterRouteFound(directionsRoute: DirectionsRoute) {
        EventSendHelper.sendEvent(MapBoxEvents.FASTER_ROUTE_FOUND, directionsRoute.toJson())

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("fasterRouteFound, %s", "New Route Distance: ${directionsRoute.distance()}"))
    }

    override fun willVoice(announcement: SpeechAnnouncement?): SpeechAnnouncement? {
        return if (FlutterMapViewFactory.voiceInstructions) {
            EventSendHelper.sendEvent(MapBoxEvents.SPEECH_ANNOUNCEMENT,
                    "{" +
                            "  \"data\": \"${announcement?.announcement()}\"" +
                            "}")

            if (FlutterMapViewFactory.debug)
                Timber.i(String.format("willVoice, %s", "SpeechAnnouncement: ${announcement?.announcement()}"))
            announcement
        } else {
            null
        }
    }

    override fun willDisplay(instructions: BannerInstructions?): BannerInstructions? {
        return if (FlutterMapViewFactory.bannerInstructions) {
            EventSendHelper.sendEvent(MapBoxEvents.BANNER_INSTRUCTION,
                    "{" +
                            "  \"data\": \"${instructions?.primary()?.text()}\"" +
                            "}")

            if (FlutterMapViewFactory.debug)
                Timber.i(String.format("willDisplay, %s", "Instructions: ${instructions?.primary()?.text()}"))
            return instructions
        } else {
            null
        }
    }

    override fun onArrival() {
        EventSendHelper.sendEvent(MapBoxEvents.ON_ARRIVAL)

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onArrival, %s", "Arrived"))
    }

    override fun onFailedReroute(errorMessage: String?) {
        EventSendHelper.sendEvent(MapBoxEvents.FAILED_TO_REROUTE,
                "{" +
                        "  \"data\": \"${errorMessage}\"" +
                        "}")

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onFailedReroute, %s", errorMessage))
    }

    override fun onOffRoute(offRoutePoint: Point?) {
        EventSendHelper.sendEvent(MapBoxEvents.USER_OFF_ROUTE,
                LocationData(
                        latitude = offRoutePoint?.latitude(),
                        longitude = offRoutePoint?.longitude()
                ).toString())

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onOffRoute, %s", "Point: ${offRoutePoint?.latitude()}, ${offRoutePoint?.longitude()}"))
    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute?) {
        EventSendHelper.sendEvent(MapBoxEvents.REROUTE_ALONG, "${directionsRoute?.toJson()}")

        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("onRerouteAlong, %s", "Distance: ${directionsRoute?.distance()}"))
    }

    override fun allowRerouteFrom(offRoutePoint: Point?): Boolean {
        if (FlutterMapViewFactory.debug)
            Timber.i(String.format("allowRerouteFrom, %s", "Point: ${offRoutePoint?.latitude()}, ${offRoutePoint?.longitude()}"))
        return true
    }

    private fun getInitialCameraPosition(): CameraPosition {
        val originCoordinate = route.routeOptions()?.coordinates()?.get(0)
        return CameraPosition.Builder()
                .target(LatLng(originCoordinate!!.latitude(), originCoordinate.longitude()))
                .zoom(FlutterMapViewFactory.zoom)
                .bearing(FlutterMapViewFactory.bearing)
                .tilt(FlutterMapViewFactory.tilt)
                .build()
    }

    private fun getDirectionsRoute(): DirectionsRoute {
        return if (FlutterMapViewFactory.testRoute.isNotEmpty()) {
            if (FlutterMapViewFactory.debug)
                Timber.i(String.format("getDirectionsRoute, %s", "Using Test Route: ${FlutterMapViewFactory.testRoute}"))
            DirectionsRoute.fromJson(FlutterMapViewFactory.testRoute.trimIndent())
        } else {
            intent.getSerializableExtra("route") as DirectionsRoute
        }
    }
}