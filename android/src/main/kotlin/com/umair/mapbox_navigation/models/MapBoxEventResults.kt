package com.umair.mapbox_navigation.models

data class MapBoxEventResults(var eventName: String, var data: String) {

    override fun toString(): String {
        return "{" +
                "  \"eventName\": \"$eventName\"," +
                "  \"data\": $data" +
                "}"
    }
}