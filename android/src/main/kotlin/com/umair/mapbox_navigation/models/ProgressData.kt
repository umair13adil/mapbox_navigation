package com.umair.mapbox_navigation.models

data class ProgressData(
        val distance: Double?, //Total distance in Metric Units of route
        val duration: Double?, //Total duration of route in hh//mm/ss
        val currentLatitude: Double?,
        val currentLongitude: Double?,
        val upcomingLatitude: Double?,
        val upcomingLongitude: Double?,
        val distanceTraveled: Double?,
        val currentLegDistanceTraveled: Double?,
        val currentLegDistanceRemaining: Double?,
        val legDistanceRemaining: Double?,
        val legDurationRemaining: Double?,
        val stepDistanceRemaining: Double?,
        var voiceInstruction: String?,
        var bannerInstruction: String?,
        var currentStepInstruction: String?, //Step Instruction
        var upComingVoiceInstruction: String?,
        var upComingBannerInstruction: String?,
        var legIndex: Int?,
        var stepIndex: Int?,
        var currentStepBearingAfter: Double?,
        var currentStepBearingBefore: Double?,
        var currentStepDrivingSide: String?,
        var currentStepExits: String?,
        var currentStepDistance: Double?,
        var currentStepDuration: Double?,
        var currentStepName: String?,
        var currentStepManeuverType: String?,
        var currentDirection: String?, //Values: [left, right, straight]
        var upComingStepBearingAfter: Double?,
        var upComingStepBearingBefore: Double?,
        var upComingStepDrivingSide: String?,
        var upComingStepExits: String?,
        var upComingStepDistance: Double?,
        var upComingStepDuration: Double?,
        var upComingStepName: String?,
        var upComingStepManeuverType: String?,
        var upComingDirection: String? //Values: [left, right]
) {
    override fun toString(): String {
        return "{" +
                "  \"distance\": $distance," +
                "  \"duration\": $duration," +
                "  \"currentLatitude\": $currentLatitude," +
                "  \"currentLongitude\": $currentLongitude," +
                "  \"upcomingLatitude\": $upcomingLatitude," +
                "  \"upcomingLongitude\": $upcomingLongitude," +
                "  \"distanceTraveled\": $distanceTraveled," +
                "  \"currentLegDistanceTraveled\": $currentLegDistanceTraveled," +
                "  \"currentLegDistanceRemaining\": $currentLegDistanceRemaining," +
                "  \"legDistanceRemaining\": $legDistanceRemaining," +
                "  \"legDurationRemaining\": $legDurationRemaining," +
                "  \"stepDistanceRemaining\": $stepDistanceRemaining," +
                "  \"voiceInstruction\": \"$voiceInstruction\"," +
                "  \"bannerInstruction\": \"$bannerInstruction\"," +
                "  \"currentStepInstruction\": \"$currentStepInstruction\"," +
                "  \"upComingVoiceInstruction\": \"$upComingVoiceInstruction\"," +
                "  \"upComingBannerInstruction\": \"$upComingBannerInstruction\"," +
                "  \"legIndex\": $legIndex," +
                "  \"stepIndex\": $stepIndex," +
                "  \"currentStepBearingAfter\": $currentStepBearingAfter," +
                "  \"currentStepBearingBefore\": $currentStepBearingBefore," +
                "  \"currentStepDrivingSide\": \"$currentStepDrivingSide\"," +
                "  \"currentStepExits\": \"$currentStepExits\"," +
                "  \"currentStepDistance\": $currentStepDistance," +
                "  \"currentStepDuration\": $currentStepDuration," +
                "  \"currentStepName\": \"$currentStepName\"," +
                "  \"currentStepManeuverType\": \"$currentStepManeuverType\"," +
                "  \"currentDirection\": \"$currentDirection\"," +
                "  \"upComingStepBearingAfter\": $upComingStepBearingAfter," +
                "  \"upComingStepBearingBefore\": $upComingStepBearingBefore," +
                "  \"upComingStepDrivingSide\": \"$upComingStepDrivingSide\"," +
                "  \"upComingStepExits\": \"$upComingStepExits\"," +
                "  \"upComingStepDistance\": $upComingStepDistance," +
                "  \"upComingStepDuration\": $upComingStepDuration," +
                "  \"upComingStepName\": \"$upComingStepName\"," +
                "  \"upComingStepManeuverType\": \"$upComingStepManeuverType\"," +
                "  \"upComingDirection\": \"$upComingDirection\"" +
                "}"
    }
}