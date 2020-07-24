package com.umair.mapbox_navigation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.umair.mapbox_navigation.mapbox.NavigationViewActivity
import com.umair.mapbox_navigation.plugin.MapViewFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.platform.PlatformView
import timber.log.Timber


class MapboxNavigationPlugin : FlutterPlugin, ActivityAware, PluginRegistry.RequestPermissionsResultListener, PlatformView {

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        setUpPluginMethods(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
    }

    companion object {
        private val TAG = "MapboxNavigationPlugin"
        private var REQUEST_LOCATION_PERMISSIONS = 1890
        private var channel: MethodChannel? = null
        private var event_channel: EventChannel? = null
        var eventSink: EventChannel.EventSink? = null
        private var currentActivity: Activity? = null

        @JvmStatic
        var binaryMessenger: BinaryMessenger? = null

        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {
            val instance = MapboxNavigationPlugin()
            registrar.addRequestPermissionsResultListener(instance)
            requestPermission()
            setUpPluginMethods(registrar.activity(), registrar.messenger())

            registrar
                    .platformViewRegistry()
                    .registerViewFactory(
                            "umair.mapbox_navigation/mapboxMapView", MapViewFactory(registrar.messenger()));
        }

        @JvmStatic
        fun registerWith(messenger: BinaryMessenger, context: Context) {
            val instance = MapboxNavigationPlugin()
            requestPermission()
            setUpPluginMethods(context, messenger)
        }

        @JvmStatic
        private fun setUpPluginMethods(context: Context, messenger: BinaryMessenger) {

            channel = MethodChannel(messenger, "mapbox_navigation")
            notifyIfPermissionsGranted(context)

            channel?.setMethodCallHandler { call, result ->
                when (call.method) {
                    "startService" -> {
                        context.let {
                            result.success("Started Navigation")
                        }
                    }
                    else -> result.notImplemented()
                }
            }

            event_channel = EventChannel(messenger, "mapbox_navigation_stream")
            event_channel?.setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    eventSink = events
                }

                override fun onCancel(arguments: Any?) {

                }
            })
        }

        @JvmStatic
        private fun notifyIfPermissionsGranted(context: Context) {
            if (permissionsGranted(context)) {
                doIfPermissionsGranted()
            }
        }

        @JvmStatic
        fun permissionsGranted(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        private fun doIfPermissionsGranted() {
            Timber.i(String.format("doIfPermissionsGranted, %s", "Starting Navigation"))
            currentActivity?.let {
                it.startActivity(Intent(it, NavigationViewActivity::class.java))
            }
        }

        @JvmStatic
        private fun requestPermission() {
            if (!arePermissionsGranted()) {
                Timber.i(String.format("requestPermission, %s", "Requesting location permissions.."))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    currentActivity?.let {
                        ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSIONS)
                    }
                            ?: Timber.e(String.format("requestPermission, %s", "Unable to request location permissions."))
                } else {
                    doIfPermissionsGranted()
                }
            } else {
                doIfPermissionsGranted()
            }
        }

        @JvmStatic
        private fun arePermissionsGranted(): Boolean {
            currentActivity?.let {
                return ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
            return false
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        currentActivity = null
        channel?.setMethodCallHandler(null)
        event_channel?.setStreamHandler(null)
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        currentActivity = activityPluginBinding.activity
        activityPluginBinding.addRequestPermissionsResultListener(this)
        requestPermission()

        if (!arePermissionsGranted()) {
            currentActivity?.let {
                it.startActivity(Intent(it, NavigationViewActivity::class.java))
            }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        currentActivity = activityPluginBinding.activity
        activityPluginBinding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivity() {
        currentActivity = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS && grantResults?.isNotEmpty()!! && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doIfPermissionsGranted()
            return true
        }
        return false
    }

    override fun getView(): View {
        return null!!
    }

    override fun dispose() {

    }
}

