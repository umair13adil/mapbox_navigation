package com.umair.mapbox_navigation.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.umair.mapbox_navigation.R
import io.flutter.plugin.common.MethodCall
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

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

fun getDoubleValueById(key: String, call: MethodCall): Double? {
    call.argument<Double>(key)?.let {
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

fun getLocaleFromCode(locale: String): Locale {
    val locales: Array<Locale> = Locale.getAvailableLocales()

    val filtered = locales.filter {
        it.country.equals(locale, ignoreCase = true)
    }

    return if (filtered.isNotEmpty()) {
        filtered.first()
    } else {
        Locale.ENGLISH
    }
}

fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}