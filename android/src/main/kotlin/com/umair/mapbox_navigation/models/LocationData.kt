package com.umair.mapbox_navigation.models

data class LocationData(var latitude: Double?, var longitude: Double?) {

    override fun toString(): String {
        return "{" +
                "  \"latitude\": $latitude," +
                "  \"longitude\": $longitude" +
                "}"
    }
}