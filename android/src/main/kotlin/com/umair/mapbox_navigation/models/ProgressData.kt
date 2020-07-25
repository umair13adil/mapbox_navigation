package com.umair.mapbox_navigation.models

data class ProgressData(
        val distance: Double?,
        val duration: Double?,
        val distanceTraveled: Double?,
        val legDistanceTraveled: Double?,
        val legDistanceRemaining: Double?,
        val legDurationRemaining: Double?,
        var legIndex: Int?,
        var stepIndex: Int?
) {
    override fun toString(): String {
        return "{" +
                "  \"distance\": \"$distance\"," +
                "  \"duration\": \"$duration\"," +
                "  \"distanceTraveled\": \"$distanceTraveled\"," +
                "  \"legDistanceTraveled\": \"$legDistanceTraveled\"," +
                "  \"legDistanceRemaining\": \"$legDistanceRemaining\"," +
                "  \"legDurationRemaining\": \"$legDurationRemaining\"," +
                "  \"legIndex\": \"$legIndex\"," +
                "  \"stepIndex\": \"$stepIndex\"" +
                "}"
    }
}