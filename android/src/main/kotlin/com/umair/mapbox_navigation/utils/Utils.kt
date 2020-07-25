package com.umair.mapbox_navigation.utils

import android.content.Context
import android.graphics.BitmapFactory
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.umair.mapbox_navigation.R
import io.flutter.plugin.common.MethodCall
import java.io.ByteArrayInputStream
import java.io.InputStream

fun getListOfStringById(key: String, call: MethodCall): ArrayList<String> {
    val logTypesList = arrayListOf<String>()
    call.argument<String>(key)?.let {
        it.split(",").forEach {
            logTypesList.add(it)
        }
        return logTypesList
    }
    return arrayListOf()
}

fun getStringValueById(key: String, call: MethodCall): String {
    call.argument<String>(key)?.let {
        return it
    }
    return ""
}

fun getIntValueById(key: String, call: MethodCall): Int? {
    call.argument<Int>(key)?.let {
        return it
    }
    return null
}

fun getBoolValueById(key: String, call: MethodCall): Boolean {
    call.argument<Boolean>(key)?.let {
        return it
    }
    return false
}

fun getInputStreamValueById(key: String, call: MethodCall): InputStream? {
    call.argument<ByteArray>(key)?.let {
        return ByteArrayInputStream(it)
    }
    return null
}

fun Context.addDestinationIconSymbolLayer(loadedMapStyle: Style) {
    loadedMapStyle.addImage("destination-icon-id",
            BitmapFactory.decodeResource(this.resources, R.drawable.mapbox_marker_icon_default))
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