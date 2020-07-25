package com.umair.mapbox_navigation.plugin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.umair.mapbox_navigation.MapboxNavigationPlugin
import com.umair.mapbox_navigation.R
import com.umair.mapbox_navigation.mapbox.NavigationActivity
import com.umair.mapbox_navigation.utils.addDestinationIconSymbolLayer
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
        OnMapReadyCallback,
        MapboxMap.OnMapClickListener {

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
            context.addDestinationIconSymbolLayer(style)
            mapboxMap.addOnMapClickListener(this)
        }
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

                        activity.startActivity(Intent(activity, NavigationActivity::class.java).putExtra("route", currentRoute))

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