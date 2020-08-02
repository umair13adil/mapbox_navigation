package com.umair.mapbox_navigation.plugin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND
import com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import com.mapbox.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine
import com.mapbox.services.android.navigation.v5.milestone.Milestone
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener
import com.mapbox.services.android.navigation.v5.navigation.*
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener
import com.mapbox.services.android.navigation.v5.route.FasterRouteListener
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.umair.mapbox_navigation.MapboxNavigationPlugin
import com.umair.mapbox_navigation.R
import com.umair.mapbox_navigation.mapbox.MapUtils
import com.umair.mapbox_navigation.mapbox.NavigationActivity
import com.umair.mapbox_navigation.models.EventSendHelper
import com.umair.mapbox_navigation.models.LocationData
import com.umair.mapbox_navigation.models.MapBoxEvents
import com.umair.mapbox_navigation.models.MileStoneData
import com.umair.mapbox_navigation.utils.*
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.platform.PlatformView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class FlutterMapViewFactory internal constructor(private val context: Context, messenger: BinaryMessenger, id: Int, private val activity: Activity) :
        PlatformView,
        MethodCallHandler,
        Application.ActivityLifecycleCallbacks,
        OnMapReadyCallback,
        ProgressChangeListener,
        OffRouteListener,
        MilestoneEventListener,
        NavigationEventListener,
        NavigationListener,
        FasterRouteListener,
        SpeechAnnouncementListener,
        BannerInstructionsListener,
        RouteListener,
        RefreshCallback {

    private val methodChannel: MethodChannel = MethodChannel(messenger, MapboxNavigationPlugin.view_name + id)
    private val options: MapboxMapOptions = MapboxMapOptions.createFromAttributes(context)
            .compassEnabled(false)
            .logoEnabled(true)
    private var locationEngine: LocationEngine? = null
    private var mapView = MapView(context, options)
    private var mapBoxMap: MapboxMap? = null
    private var currentRoute: DirectionsRoute? = null
    private val routeRefresh = RouteRefresh(Mapbox.getAccessToken()!!)
    private var navigationMapRoute: NavigationMapRoute? = null
    private val navigationOptions = MapboxNavigationOptions.Builder()
            .build()
    private var navigation: MapboxNavigation = MapboxNavigation(
            context,
            Mapbox.getAccessToken()!!,
            navigationOptions
    )
    private var mapReady = false
    private lateinit var markerViewManager: MarkerViewManager
    private var initialMarkerView: MarkerView? = null
    private var locationMarkerView: MarkerView? = null

    private var isDisposed = false
    private var isRefreshing = false
    private var isNavigationCanceled = false
    private var activityHashCode = activity.hashCode()

    companion object {

        //Config
        var initialLat = 0.0
        var initialLong = 0.0
        var originLat = 0.0
        var originLong = 0.0
        var destinationLat = 0.0
        var destinationLong = 0.0
        var shouldSimulateRoute = false
        var language = "en"
        var zoom = 15.0
        var bearing = 0.0
        var tilt = 0.0
        var alternatives = false
        var clientAppName = "MapBox Client"
        var profile = "driving"
        var continueStraight = false
        var enableRefresh = false
        var steps = true
        var voiceInstructions = true
        var bannerInstructions = true
        var testRoute = ""
        var debug = true
    }

    override fun getView(): View {
        return mapView
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "showMapView" -> {
                showMapView(methodCall, result)
            }
            "buildRoute" -> {
                buildRoute(methodCall, result)
            }
            "addMarker" -> {
                addMarker(methodCall, result)
            }
            "moveCameraToPosition" -> {
                moveCamera(methodCall, result)
            }
            "startNavigation" -> {
                startNavigation(methodCall, result)
            }
            "startEmbeddedNavigation" -> {
                startEmbeddedNavigation(methodCall, result)
            }
            "stopNavigation" -> {
                cancelEmbeddedNavigation(methodCall, result)
            }
            else -> result.notImplemented()
        }
    }

    override fun dispose() {
        isDisposed = true
        mapReady = false
        mapView.onStop()
        mapView.onDestroy()
        if (debug)
            Timber.i(String.format("dispose, %s", ""))
    }

    init {
        activity.application.registerActivityLifecycleCallbacks(this)
        methodChannel.setMethodCallHandler(this)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap1: MapboxMap) {
        if (debug)
            Timber.i(String.format("onMapReady, %s", "Map is ready."))

        this.mapReady = true
        this.mapBoxMap = mapboxMap1
        locationEngine = ReplayRouteLocationEngine()

        mapBoxMap?.setStyle(context.getString(R.string.navigation_guidance_day)) { style ->
            context.addDestinationIconSymbolLayer(style)
            enableLocationComponent(style)

            val routeLineLayer = LineLayer("line-layer-id", "source-id")
            routeLineLayer.setProperties(
                    lineWidth(9f),
                    lineColor(Color.RED),
                    lineCap(LINE_CAP_ROUND),
                    lineJoin(LINE_JOIN_ROUND)
            )
            style.addLayer(routeLineLayer)
        }

        markerViewManager = MarkerViewManager(mapView, mapBoxMap)
        moveCamera(LatLng(initialLat, initialLong))

        EventSendHelper.sendEvent(MapBoxEvents.MAP_READY)
    }

    private fun moveCamera(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
                .target(location)
                .zoom(zoom)
                .bearing(bearing)
                .tilt(tilt)
                .build()

        mapBoxMap?.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition), 3000)
    }

    private fun isLocationValid(): Boolean {
        return destinationLong != 0.0 && destinationLat != 0.0 && originLong != 0.0 && originLat != 0.0
    }

    private fun buildRoute(result: MethodChannel.Result) {
        if (mapReady) {
            if (isLocationValid()) {
                val destinationPoint = Point.fromLngLat(destinationLong, destinationLat)
                val originPoint = Point.fromLngLat(originLong, originLat)
                val source = mapBoxMap?.style?.getSourceAs<GeoJsonSource>("destination-source-id")
                source?.setGeoJson(Feature.fromGeometry(destinationPoint))
                getRoute(originPoint, destinationPoint, context)
                result.success("Building route.")
            } else {
                result.success("Unable to build route, Invalid location provided.")
            }
        } else {
            result.success("Unable to build route, map is not ready. Try again.")
        }
    }

    private fun addCustomMarker(location: LatLng, @DrawableRes markerIcon: Int, rotationFrom: Double? = null, rotationTo: Double? = null) {
        if (initialMarkerView != null) {
            markerViewManager.removeMarker(initialMarkerView!!)
        }

        val markerView = ImageView(context)
        markerView.setImageResource(markerIcon)
        markerView.layoutParams = ViewGroup.LayoutParams(100, 100)

        rotationTo?.let {
            markerView.rotation = rotationTo.toFloat()
            /*rotationFrom?.let {
                val rotate = RotateAnimation(rotationFrom.toFloat(), rotationTo.toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 800
                rotate.interpolator = LinearInterpolator()
                rotate.isFillEnabled = true
                //rotate.fillAfter = true
                markerView.startAnimation(rotate)
            }*/
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            markerView.elevation = 16.0f
        }
        initialMarkerView = MarkerView(location, markerView)
        initialMarkerView?.let {
            markerViewManager.addMarker(it)
        }
    }

    private fun addLocationMarker(location: LatLng, @DrawableRes markerIcon: Int) {
        if (locationMarkerView != null) {
            markerViewManager.removeMarker(locationMarkerView!!)
        }

        val markerView = ImageView(context)
        markerView.setImageResource(markerIcon)
        markerView.layoutParams = ViewGroup.LayoutParams(100, 100)

        locationMarkerView = MarkerView(location, markerView)
        locationMarkerView?.let {
            markerViewManager.addMarker(it)
        }
    }


    private fun getRoute(origin: Point, destination: Point, context: Context) {
        NavigationRoute.builder(context)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .language(getLocaleFromCode(language))
                .alternatives(alternatives)
                .clientAppName(clientAppName)
                .profile(profile)
                .continueStraight(continueStraight)
                .enableRefresh(enableRefresh)
                .voiceUnits(DirectionsCriteria.METRIC)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>) {

                        if (debug)
                            Timber.i(String.format("Response code, %s", response.code()))

                        if (response.body() == null) {
                            if (debug)
                                Timber.e(String.format("No routes found, make sure you set the right user and access token., %s", response.code()))
                            return
                        } else if (response.body()!!.routes().size < 1) {
                            if (debug)
                                Timber.e(String.format("No routes found, %s", response.code()))
                            return
                        }
                        currentRoute = response.body()!!.routes()[0]
                        EventSendHelper.sendEvent(MapBoxEvents.ROUTE_BUILT, data = "${response.body()?.toJson()}")

                        moveCameraToOriginOfRoute()

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute?.removeRoute()
                        } else {
                            navigationMapRoute = NavigationMapRoute(navigation, mapView, mapBoxMap!!, R.style.NavigationMapRoute)
                        }
                        navigationMapRoute?.addRoute(currentRoute)
                    }

                    override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                        Timber.e(String.format("Error, %s", throwable.message))
                    }
                })
    }

    private fun moveCameraToOriginOfRoute() {
        currentRoute?.let {
            val originCoordinate = it.routeOptions()?.coordinates()?.get(0)
            originCoordinate?.let {
                val location = LatLng(originCoordinate.latitude(), originCoordinate.longitude())
                moveCamera(location)
                //addCustomMarker(location)
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //Timber.i(String.format("onActivityCreated, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        mapView.onCreate(savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) {
        //Timber.i(String.format("onActivityStarted, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        try {
            mapView.onStart()
        } catch (e: java.lang.Exception) {
            Timber.i(String.format("onActivityStarted, %s", "Error: ${e.message}"))
        }
    }

    override fun onActivityResumed(activity: Activity) {
        //Timber.i(String.format("onActivityResumed, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        mapView.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        //Timber.i(String.format("onActivityPaused, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        mapView.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        //Timber.i(String.format("onActivityStopped, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        //mapView.onStop()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
        //Timber.i(String.format("onActivitySaveInstanceState, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        mapView.onSaveInstanceState(outState!!)
    }

    override fun onActivityDestroyed(activity: Activity) {
        //Timber.i(String.format("onActivityDestroyed, %s, %s, %s", "$activityHashCode", "${activity.hashCode()}", "$isDisposed"))
        //mapView.onDestroy()
    }

    override fun onRefresh(directionsRoute: DirectionsRoute) {
        if (debug)
            Timber.i(String.format("onRefresh, %s", "New Route Distance: ${directionsRoute.distance()}"))

        navigation.startNavigation(directionsRoute)
        isRefreshing = false
    }

    override fun onError(error: RefreshError) {
        if (debug)
            Timber.i(String.format("onRefresh, %s", "Error: ${error.message}"))
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {

        MapUtils.doOnProgressChange(location, routeProgress)

        addCustomMarker(LatLng(location.latitude, location.longitude), R.drawable.mapbox_marker_icon_default)

        moveCamera(LatLng(location.latitude, location.longitude))

        if (shouldSimulateRoute)
            mapBoxMap?.locationComponent?.forceLocationUpdate(location)

        if (!isRefreshing) {
            isRefreshing = true
            routeRefresh.refresh(routeProgress, this)
        }
    }

    override fun userOffRoute(location: Location) {

        EventSendHelper.sendEvent(MapBoxEvents.USER_OFF_ROUTE,
                LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude
                ).toString())

        if (debug)
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

        if (debug)
            Timber.i(String.format("onMilestoneEvent, %s, %s, %s",
                    "Distance Remaining: ${routeProgress.currentLegProgress?.distanceRemaining}",
                    "Instruction: $instruction",
                    "Milestone: ${milestone.instruction}"
            ))
    }

    override fun onRunning(running: Boolean) {

        if (!isNavigationCanceled) {
            EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_RUNNING)

            if (debug)
                Timber.i(String.format("onRunning, %s", "$running"))
        }
    }

    override fun onCancelNavigation() {
        EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_CANCELLED)

        navigation.stopNavigation()
        if (debug)
            Timber.i(String.format("onCancelNavigation, %s", ""))
    }

    override fun onNavigationFinished() {
        EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_FINISHED)

        if (debug)
            Timber.i(String.format("onNavigationFinished, %s", ""))
    }

    override fun onNavigationRunning() {
        if (!isNavigationCanceled) {
            EventSendHelper.sendEvent(MapBoxEvents.NAVIGATION_RUNNING)

            if (debug)
                Timber.i(String.format("onNavigationRunning, %s", ""))
        }
    }

    override fun fasterRouteFound(directionsRoute: DirectionsRoute) {
        EventSendHelper.sendEvent(MapBoxEvents.FASTER_ROUTE_FOUND, directionsRoute.toJson())

        if (debug)
            Timber.i(String.format("fasterRouteFound, %s", "New Route Distance: ${directionsRoute.distance()}"))
    }

    override fun willVoice(announcement: SpeechAnnouncement?): SpeechAnnouncement? {
        return if (FlutterMapViewFactory.voiceInstructions) {
            EventSendHelper.sendEvent(MapBoxEvents.SPEECH_ANNOUNCEMENT,
                    "{" +
                            "  \"data\": \"${announcement?.announcement()}\"" +
                            "}")

            if (debug)
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

            if (debug)
                Timber.i(String.format("willDisplay, %s", "Instructions: ${instructions?.primary()?.text()}"))
            return instructions
        } else {
            null
        }
    }

    override fun onArrival() {
        EventSendHelper.sendEvent(MapBoxEvents.ON_ARRIVAL)

        if (debug)
            Timber.i(String.format("onArrival, %s", "Arrived"))
    }

    override fun onFailedReroute(errorMessage: String?) {
        EventSendHelper.sendEvent(MapBoxEvents.FAILED_TO_REROUTE,
                "{" +
                        "  \"data\": \"${errorMessage}\"" +
                        "}")

        if (debug)
            Timber.i(String.format("onFailedReroute, %s", errorMessage))
    }

    override fun onOffRoute(offRoutePoint: Point?) {
        EventSendHelper.sendEvent(MapBoxEvents.USER_OFF_ROUTE,
                LocationData(
                        latitude = offRoutePoint?.latitude(),
                        longitude = offRoutePoint?.longitude()
                ).toString())

        if (debug)
            Timber.i(String.format("onOffRoute, %s", "Point: ${offRoutePoint?.latitude()}, ${offRoutePoint?.longitude()}"))
    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute?) {
        EventSendHelper.sendEvent(MapBoxEvents.REROUTE_ALONG, "${directionsRoute?.toJson()}")

        if (debug)
            Timber.i(String.format("onRerouteAlong, %s", "Distance: ${directionsRoute?.distance()}"))
    }

    override fun allowRerouteFrom(offRoutePoint: Point?): Boolean {
        if (debug)
            Timber.i(String.format("allowRerouteFrom, %s", "Point: ${offRoutePoint?.latitude()}, ${offRoutePoint?.longitude()}"))
        return true
    }


    private fun enableLocationComponent(@NonNull loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            val customLocationComponentOptions = LocationComponentOptions.builder(context)
                    .pulseEnabled(true)
                    .build()
            mapBoxMap?.locationComponent?.let { locationComponent ->
                try {
                    locationComponent.activateLocationComponent(
                            LocationComponentActivationOptions.builder(context, loadedMapStyle)
                                    .locationComponentOptions(customLocationComponentOptions)
                                    .build())
                    locationComponent.isLocationComponentEnabled = true
                    locationComponent.cameraMode = CameraMode.TRACKING
                    locationComponent.renderMode = RenderMode.NORMAL

                    locationComponent.lastKnownLocation?.let {
                        val location = LatLng(it.latitude, it.longitude)
                        moveCamera(location)
                    }
                } catch (e: Exception) {
                    Timber.i(String.format("enableLocationComponent, %s", "Error: ${e.message}"))
                }
            }
        }
    }

    private fun showMapView(methodCall: MethodCall, result: MethodChannel.Result) {
        getDoubleValueById("initialLat", methodCall).takeIf { it != null }?.let {
            initialLat = it
        }
        getDoubleValueById("initialLong", methodCall).takeIf { it != null }?.let {
            initialLong = it
        }
        getDoubleValueById("zoom", methodCall).takeIf { it != null }?.let {
            zoom = it
        }
        getDoubleValueById("bearing", methodCall).takeIf { it != null }?.let {
            bearing = it
        }
        getDoubleValueById("tilt", methodCall).takeIf { it != null }?.let {
            tilt = it
        }

        language = getStringValueById("language", methodCall)
        alternatives = getBoolValueById("alternatives", methodCall)
        clientAppName = getStringValueById("clientAppName", methodCall)
        profile = getStringValueById("profile", methodCall)
        continueStraight = getBoolValueById("continueStraight", methodCall)
        enableRefresh = getBoolValueById("enableRefresh", methodCall)
        steps = getBoolValueById("steps", methodCall)
        voiceInstructions = getBoolValueById("voiceInstructions", methodCall)
        bannerInstructions = getBoolValueById("bannerInstructions", methodCall)
        testRoute = getStringValueById("testRoute", methodCall)
        debug = getBoolValueById("debug", methodCall)

        result.success("MapView options set.")
    }

    private fun buildRoute(methodCall: MethodCall, result: MethodChannel.Result) {
        isNavigationCanceled = false
        getDoubleValueById("originLat", methodCall).takeIf { it != null }?.let {
            originLat = it
        }
        getDoubleValueById("originLong", methodCall).takeIf { it != null }?.let {
            originLong = it
        }
        getDoubleValueById("destinationLat", methodCall).takeIf { it != null }?.let {
            destinationLat = it
        }
        getDoubleValueById("destinationLong", methodCall).takeIf { it != null }?.let {
            destinationLong = it
        }
        getDoubleValueById("zoom", methodCall).takeIf { it != null }?.let {
            zoom = it
        }
        buildRoute(result)
    }

    private fun startNavigation(methodCall: MethodCall, result: MethodChannel.Result) {
        isNavigationCanceled = false
        shouldSimulateRoute = getBoolValueById("shouldSimulateRoute", methodCall)
        if (currentRoute != null) {
            activity.startActivity(Intent(activity, NavigationActivity::class.java).putExtra("route", currentRoute))
            result.success("Navigation started.")
        } else {
            result.success("No route found. Unable to start navigation.")
        }
    }

    private fun startEmbeddedNavigation(methodCall: MethodCall, result: MethodChannel.Result) {
        isNavigationCanceled = false
        getDoubleValueById("zoom", methodCall).takeIf { it != null }?.let {
            zoom = it
        }
        getDoubleValueById("bearing", methodCall).takeIf { it != null }?.let {
            bearing = it
        }
        getDoubleValueById("tilt", methodCall).takeIf { it != null }?.let {
            tilt = it
        }
        shouldSimulateRoute = getBoolValueById("shouldSimulateRoute", methodCall)
        if (currentRoute != null) {
            navigation.addOffRouteListener(this@FlutterMapViewFactory)
            navigation.addFasterRouteListener(this@FlutterMapViewFactory)
            navigation.addProgressChangeListener(this@FlutterMapViewFactory)
            navigation.addMilestoneEventListener(this@FlutterMapViewFactory)
            navigation.addNavigationEventListener(this@FlutterMapViewFactory)

            currentRoute?.let {
                if (shouldSimulateRoute) {
                    (locationEngine as ReplayRouteLocationEngine).assign(it)
                    navigation.locationEngine = locationEngine as ReplayRouteLocationEngine
                }
                navigation.startNavigation(it)
            }
            result.success("Embedded Navigation started.")
        } else {
            result.success("No route found. Unable to start navigation.")
        }
    }

    private fun cancelEmbeddedNavigation(methodCall: MethodCall, result: MethodChannel.Result) {
        zoom = 15.0
        bearing = 0.0
        tilt = 0.0
        isNavigationCanceled = true
        moveCameraToOriginOfRoute()
        if (currentRoute != null) {
            navigation.stopNavigation()
            navigation.removeFasterRouteListener(this)
            navigation.removeMilestoneEventListener(this)
            navigation.removeNavigationEventListener(this)
            navigation.removeOffRouteListener(this)
            navigation.removeProgressChangeListener(this)
            result.success("Navigation stopped.")
        } else {
            result.success("No route found. Unable to stop navigation.")
        }
    }

    private fun addMarker(methodCall: MethodCall, result: MethodChannel.Result) {
        val latitude = getDoubleValueById("latitude", methodCall)
        val longitude = getDoubleValueById("longitude", methodCall)

        if (latitude != null && longitude != null) {
            val location = LatLng(latitude, longitude)
            addCustomMarker(location, R.drawable.map_marker_dark)
            result.success("Marker Added.")
        } else {
            result.success("Unable to add marker, location invalid.")
        }
    }

    private fun moveCamera(methodCall: MethodCall, result: MethodChannel.Result) {
        val latitude = getDoubleValueById("latitude", methodCall)
        val longitude = getDoubleValueById("longitude", methodCall)

        if (latitude != null && longitude != null) {
            val location = LatLng(latitude, longitude)
            moveCamera(location)
            result.success("Camera Moved.")
        } else {
            result.success("Unable to move camera, location invalid.")
        }
    }
}