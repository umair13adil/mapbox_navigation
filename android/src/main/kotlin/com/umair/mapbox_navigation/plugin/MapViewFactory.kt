package com.umair.mapbox_navigation.plugin

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.NonNull
import com.mapbox.mapboxsdk.Mapbox
import com.umair.mapbox_navigation.R
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import timber.log.Timber

class MapViewFactory(private val messenger: BinaryMessenger, private val activity: Activity, private var accessToken:String) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        Mapbox.getInstance(activity, accessToken)
        return FlutterMapViewFactory(context!!, messenger, viewId, activity)
    }
}