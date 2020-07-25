package com.umair.mapbox_navigation.plugin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
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
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.umair.mapbox_navigation.MapboxNavigationPlugin
import com.umair.mapbox_navigation.R
import com.umair.mapbox_navigation.mapbox.NavigationActivity
import com.umair.mapbox_navigation.models.EventSendHelper
import com.umair.mapbox_navigation.models.MapBoxEvents
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
import java.util.*

class FlutterMapViewFactory internal constructor(private val context: Context, messenger: BinaryMessenger, id: Int, val activity: Activity) :
        PlatformView,
        MethodCallHandler,
        Application.ActivityLifecycleCallbacks,
        OnMapReadyCallback {

    private val methodChannel: MethodChannel = MethodChannel(messenger, MapboxNavigationPlugin.view_name + id)
    private val options: MapboxMapOptions = MapboxMapOptions.createFromAttributes(context)
    private var mapView = MapView(context, options)
    private var mapBoxMap: MapboxMap? = null
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var mapReady = false
    private lateinit var markerViewManager: MarkerViewManager
    private var initialMarkerView: MarkerView? = null

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
        var bearing = 180.0
        var tilt = 30.0
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
                getDoubleValueById("initialLat", methodCall).takeIf { it != null }?.let {
                    initialLat = it
                }
                getDoubleValueById("initialLong", methodCall).takeIf { it != null }?.let {
                    initialLong = it
                }
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
                getDoubleValueById("bearing", methodCall).takeIf { it != null }?.let {
                    bearing = it
                }
                getDoubleValueById("tilt", methodCall).takeIf { it != null }?.let {
                    tilt = it
                }

                shouldSimulateRoute = getBoolValueById("shouldSimulateRoute", methodCall)
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

                val location = LatLng(originLat, originLong)
                moveCamera(location)
                addCustomMarker(location)

                result.success("MapView options set.")
            }
            "buildRoute" -> {
                buildRoute(result)
            }
            "startNavigation" -> {
                if (currentRoute != null) {
                    activity.startActivity(Intent(activity, NavigationActivity::class.java).putExtra("route", currentRoute))
                    result.success("Navigation started.")
                } else {
                    result.success("No route found. Unable to start navigation.")
                }
            }
            else -> result.notImplemented()
        }
    }

    override fun dispose() {
        mapReady = false
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

    private fun buildRoute(result: MethodChannel.Result) {
        if (mapReady) {
            val destinationPoint = Point.fromLngLat(destinationLong, destinationLat)
            val originPoint = Point.fromLngLat(originLong, originLat)
            val source = mapBoxMap?.style?.getSourceAs<GeoJsonSource>("destination-source-id")
            source?.setGeoJson(Feature.fromGeometry(destinationPoint))
            getRoute(originPoint, destinationPoint, context)
            result.success("Building route.")
        } else {
            result.success("Unable to build route, map is not ready. Try again.")
        }
    }

    private fun addCustomMarker(location: LatLng) {
        if (initialMarkerView != null) {
            markerViewManager.removeMarker(initialMarkerView!!)
        }

        val markerView = ImageView(context)
        markerView.setImageResource(R.drawable.map_marker_dark)
        markerView.layoutParams = ViewGroup.LayoutParams(100, 100)

        initialMarkerView = MarkerView(location, markerView)
        initialMarkerView?.let {
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

                        val originCoordinate = currentRoute?.routeOptions()?.coordinates()?.get(0)
                        originCoordinate?.let {
                            val location = LatLng(originCoordinate.latitude(), originCoordinate.longitude())
                            moveCamera(location)
                            addCustomMarker(location)
                        }

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute?.removeRoute()
                        } else {
                            navigationMapRoute = NavigationMapRoute(null, mapView, mapBoxMap!!, R.style.NavigationMapRoute)
                        }
                        navigationMapRoute?.addRoute(currentRoute)
                    }

                    override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                        Timber.e(String.format("Error, %s", throwable.message))
                    }
                })
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.i(String.format("onActivityCreated, %s", ""))
        mapView.onCreate(savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.i(String.format("onActivityStarted, %s", ""))
        mapView.onStart()
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.i(String.format("onActivityResumed, %s", ""))
        mapView.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.i(String.format("onActivityPaused, %s", ""))
        mapView.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.i(String.format("onActivityStopped, %s", ""))
        mapView.onStop()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
        Timber.i(String.format("onActivitySaveInstanceState, %s", ""))
        mapView.onSaveInstanceState(outState!!)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.i(String.format("onActivityDestroyed, %s", ""))
        mapView.onDestroy()
    }

    private fun enableLocationComponent(@NonNull loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            val customLocationComponentOptions = LocationComponentOptions.builder(context)
                    .pulseEnabled(true)
                    .build()
            mapBoxMap?.locationComponent?.let { locationComponent ->
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(context, loadedMapStyle)
                                .locationComponentOptions(customLocationComponentOptions)
                                .build())
                locationComponent.isLocationComponentEnabled = true
                locationComponent.cameraMode = CameraMode.TRACKING
                locationComponent.renderMode = RenderMode.NORMAL
            }
        }
    }
}