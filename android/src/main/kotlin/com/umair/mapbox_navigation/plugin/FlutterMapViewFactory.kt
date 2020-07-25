package com.umair.mapbox_navigation.plugin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import com.mapbox.services.android.navigation.v5.milestone.Milestone
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener
import com.mapbox.services.android.navigation.v5.route.FasterRouteListener
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.umair.mapbox_navigation.MapboxNavigationPlugin
import com.umair.mapbox_navigation.R
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.platform.PlatformView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class FlutterMapViewFactory internal constructor(val context: Context, messenger: BinaryMessenger, id: Int, activity: Activity) :
        PlatformView,
        MethodCallHandler,
        Application.ActivityLifecycleCallbacks,
        OnMapReadyCallback,
        MapboxMap.OnMapClickListener,
        ProgressChangeListener,
        OffRouteListener,
        MilestoneEventListener,
        NavigationEventListener,
        NavigationListener,
        FasterRouteListener,
        SpeechAnnouncementListener,
        BannerInstructionsListener,
        RouteListener {

    private val methodChannel: MethodChannel = MethodChannel(messenger, MapboxNavigationPlugin.view_name + id)
    private val options: MapboxMapOptions = MapboxMapOptions.createFromAttributes(context)
    private var mapView = MapView(context, options)
    private var mapboxMap: MapboxMap? = null
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    override fun getView(): View {
        Timber.i(String.format("getView, %s", ""))
        return mapView
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "showMapView" -> showMapView(methodCall, result)
            else -> result.notImplemented()
        }
    }

    private fun showMapView(methodCall: MethodCall, result: MethodChannel.Result) {
        val text = methodCall.arguments as String
        Timber.i(String.format("showMapView, %s", text))
        result.success(null)
    }

    override fun dispose() {
        Timber.i(String.format("dispose, %s", ""))
    }

    init {
        activity.application.registerActivityLifecycleCallbacks(this)
        methodChannel.setMethodCallHandler(this)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(context.getString(R.string.navigation_guidance_day)) { style ->
            addDestinationIconSymbolLayer(style)
            mapboxMap.addOnMapClickListener(this)
        }
    }

    private fun addDestinationIconSymbolLayer(loadedMapStyle: Style) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(context.resources, R.drawable.mapbox_marker_icon_default))
        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)
        val destinationSymbolLayer = SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(
                PropertyFactory.iconImage("destination-icon-id"),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
        )
        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

    override fun onMapClick(point: LatLng): Boolean {
        val destinationPoint = Point.fromLngLat(73.25372189, 33.79862353)
        val originPoint = Point.fromLngLat(73.0651511, 33.6938118)
        val source = mapboxMap?.style?.getSourceAs<GeoJsonSource>("destination-source-id")
        source?.setGeoJson(Feature.fromGeometry(destinationPoint))
        getRoute(originPoint, destinationPoint, context)
        return true
    }

    private fun getRoute(origin: Point, destination: Point, context: Context) {
        NavigationRoute.builder(context)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .language(Locale.US)
                .voiceUnits(DirectionsCriteria.METRIC)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>) {
                        // You can get the generic HTTP info about the response
                        Timber.i(String.format("Response code, %s", response.code()))

                        if (response.body() == null) {
                            Timber.e(String.format("No routes found, make sure you set the right user and access token., %s", response.code()))
                            return
                        } else if (response.body()!!.routes().size < 1) {
                            Timber.e(String.format("No routes found, %s", response.code()))
                            return
                        }
                        currentRoute = response.body()!!.routes()[0]

//                        val options = NavigationLauncherOptions.builder()
//                                .directionsRoute(currentRoute)
//                                .shouldSimulateRoute(false)
//                                .build()
//                        NavigationLauncher.startNavigation(activity, options)

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute?.removeRoute()
                        } else {
                            navigationMapRoute = NavigationMapRoute(null, mapView, mapboxMap!!, R.style.NavigationMapRoute)
                        }
                        navigationMapRoute?.addRoute(currentRoute)
                    }

                    override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                        Timber.e(String.format("Error, %s", throwable.message))
                    }
                })
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        Timber.i(String.format("onProgressChange, %s, %s", "Current Location: ${location.latitude},${location.longitude}",
                "Distance Remaining: ${routeProgress.currentLegProgress?.distanceRemaining}"))
    }

    override fun userOffRoute(location: Location) {
        Timber.i(String.format("userOffRoute, %s", "Current Location: ${location.latitude},${location.longitude}"))
    }

    override fun onMilestoneEvent(routeProgress: RouteProgress, instruction: String, milestone: Milestone) {
        Timber.i(String.format("onMilestoneEvent, %s, %s, %s",
                "Distance Remaining: ${routeProgress.currentLegProgress?.distanceRemaining}",
                "Instruction: $instruction",
                "Milestone: ${milestone.instruction}"
        ))
    }

    override fun onRunning(running: Boolean) {
        Timber.i(String.format("onRunning, %s", "$running"))
    }

    override fun onCancelNavigation() {
        Timber.i(String.format("onCancelNavigation, %s", ""))
    }

    override fun onNavigationFinished() {
        Timber.i(String.format("onNavigationFinished, %s", ""))
    }

    override fun onNavigationRunning() {
        Timber.i(String.format("onNavigationRunning, %s", ""))
    }

    override fun fasterRouteFound(directionsRoute: DirectionsRoute) {
        Timber.i(String.format("fasterRouteFound, %s", "New Route Distance: ${directionsRoute.distance()}"))
    }

    override fun willVoice(announcement: SpeechAnnouncement?): SpeechAnnouncement {
        Timber.i(String.format("willVoice, %s", "SpeechAnnouncement: ${announcement?.announcement()}"))
        return announcement!!
    }

    override fun willDisplay(instructions: BannerInstructions?): BannerInstructions {
        Timber.i(String.format("willDisplay, %s", "Instructions: ${instructions?.primary()?.text()}"))
        return instructions!!
    }

    override fun onArrival() {
        Timber.i(String.format("onArrival, %s", "Arrived"))
    }

    override fun onFailedReroute(errorMessage: String?) {
        Timber.i(String.format("onFailedReroute, %s", errorMessage))
    }

    override fun onOffRoute(offRoutePoint: Point?) {
        Timber.i(String.format("onOffRoute, %s", "Point: ${offRoutePoint?.latitude()}, ${offRoutePoint?.longitude()}"))
    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute?) {
        Timber.i(String.format("onRerouteAlong, %s", "Distance: ${directionsRoute?.distance()}"))
    }

    override fun allowRerouteFrom(offRoutePoint: Point?): Boolean {
        Timber.i(String.format("allowRerouteFrom, %s", "Point: ${offRoutePoint?.latitude()}, ${offRoutePoint?.longitude()}"))
        return true
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) {
        mapView.onStart()
    }

    override fun onActivityResumed(activity: Activity) {
        mapView.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        mapView.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        mapView.onStop()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
        mapView.onSaveInstanceState(outState!!)
    }

    override fun onActivityDestroyed(activity: Activity) {
        mapView.onDestroy()
    }
}