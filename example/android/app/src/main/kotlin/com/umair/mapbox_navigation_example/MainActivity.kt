package com.umair.mapbox_navigation_example

import androidx.annotation.NonNull
import com.umair.mapbox_navigation.MapboxNavigationPlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity : FlutterActivity() {

    companion object {

        @JvmStatic
        var flutterEngineInstance: FlutterEngine? = null
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        flutterEngineInstance = flutterEngine
    }

    override fun onResume() {
        super.onResume()

        flutterEngineInstance?.let {
            MapboxNavigationPlugin.registerWith(it, getString(R.string.access_token))
        }
    }

    override fun onDestroy() {
        flutterEngine?.platformViewsController?.onFlutterViewDestroyed()
        super.onDestroy()
    }
}
