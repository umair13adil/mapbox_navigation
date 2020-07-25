package com.umair.mapbox_navigation_example

import androidx.annotation.NonNull
import com.umair.mapbox_navigation.MapboxNavigationPlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        MapboxNavigationPlugin.registerWith(flutterEngine, this, getString(R.string.access_token))
    }
}
