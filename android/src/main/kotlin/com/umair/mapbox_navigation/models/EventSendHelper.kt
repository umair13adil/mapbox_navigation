package com.umair.mapbox_navigation.models

import com.umair.mapbox_navigation.MapboxNavigationPlugin

object EventSendHelper {

    fun sendEvent(event: MapBoxEvents, data: String = "{}") {
        MapboxNavigationPlugin.eventSink?.success(MapBoxEventResults(event.value, data).toString())
    }
}