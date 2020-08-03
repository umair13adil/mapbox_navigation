class MapBoxProgressEvent {
  double distance;
  double duration;
  double currentLatitude;
  double currentLongitude;
  double upcomingLatitude;
  double upcomingLongitude;
  double distanceTraveled;
  double currentLegDistanceTraveled;
  double currentLegDistanceRemaining;
  double legDistanceRemaining;
  double legDurationRemaining;
  double stepDistanceRemaining;
  String voiceInstruction;
  String bannerInstruction;
  String upComingVoiceInstruction;
  String upComingBannerInstruction;
  int legIndex;
  int stepIndex;
  double currentStepBearingAfter;
  double currentStepBearingBefore;
  String currentStepDrivingSide;
  String currentStepExits;
  double currentStepDistance;
  double currentStepDuration;
  String currentStepName;
  double upComingStepBearingAfter;
  double upComingStepBearingBefore;
  String upComingStepDrivingSide;
  String upComingStepExits;
  double upComingStepDistance;
  double upComingStepDuration;
  String upComingStepName;

  MapBoxProgressEvent(
      {this.distance,
        this.duration,
        this.currentLatitude,
        this.currentLongitude,
        this.upcomingLatitude,
        this.upcomingLongitude,
        this.distanceTraveled,
        this.currentLegDistanceTraveled,
        this.currentLegDistanceRemaining,
        this.legDistanceRemaining,
        this.legDurationRemaining,
        this.stepDistanceRemaining,
        this.voiceInstruction,
        this.bannerInstruction,
        this.upComingVoiceInstruction,
        this.upComingBannerInstruction,
        this.legIndex,
        this.stepIndex,
        this.currentStepBearingAfter,
        this.currentStepBearingBefore,
        this.currentStepDrivingSide,
        this.currentStepExits,
        this.currentStepDistance,
        this.currentStepDuration,
        this.currentStepName,
        this.upComingStepBearingAfter,
        this.upComingStepBearingBefore,
        this.upComingStepDrivingSide,
        this.upComingStepExits,
        this.upComingStepDistance,
        this.upComingStepDuration,
        this.upComingStepName});

  MapBoxProgressEvent.fromJson(Map<String, dynamic> json) {
    distance = json['distance'];
    duration = json['duration'];
    currentLatitude = json['currentLatitude'];
    currentLongitude = json['currentLongitude'];
    upcomingLatitude = json['upcomingLatitude'];
    upcomingLongitude = json['upcomingLongitude'];
    distanceTraveled = json['distanceTraveled'];
    currentLegDistanceTraveled = json['currentLegDistanceTraveled'];
    currentLegDistanceRemaining = json['currentLegDistanceRemaining'];
    legDistanceRemaining = json['legDistanceRemaining'];
    legDurationRemaining = json['legDurationRemaining'];
    stepDistanceRemaining = json['stepDistanceRemaining'];
    voiceInstruction = json['voiceInstruction'];
    bannerInstruction = json['bannerInstruction'];
    upComingVoiceInstruction = json['upComingVoiceInstruction'];
    upComingBannerInstruction = json['upComingBannerInstruction'];
    legIndex = json['legIndex'];
    stepIndex = json['stepIndex'];
    currentStepBearingAfter = json['currentStepBearingAfter'];
    currentStepBearingBefore = json['currentStepBearingBefore'];
    currentStepDrivingSide = json['currentStepDrivingSide'];
    currentStepExits = json['currentStepExits'];
    currentStepDistance = json['currentStepDistance'];
    currentStepDuration = json['currentStepDuration'];
    currentStepName = json['currentStepName'];
    upComingStepBearingAfter = json['upComingStepBearingAfter'];
    upComingStepBearingBefore = json['upComingStepBearingBefore'];
    upComingStepDrivingSide = json['upComingStepDrivingSide'];
    upComingStepExits = json['upComingStepExits'];
    upComingStepDistance = json['upComingStepDistance'];
    upComingStepDuration = json['upComingStepDuration'];
    upComingStepName = json['upComingStepName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distance'] = this.distance;
    data['duration'] = this.duration;
    data['currentLatitude'] = this.currentLatitude;
    data['currentLongitude'] = this.currentLongitude;
    data['upcomingLatitude'] = this.upcomingLatitude;
    data['upcomingLongitude'] = this.upcomingLongitude;
    data['distanceTraveled'] = this.distanceTraveled;
    data['currentLegDistanceTraveled'] = this.currentLegDistanceTraveled;
    data['currentLegDistanceRemaining'] = this.currentLegDistanceRemaining;
    data['legDistanceRemaining'] = this.legDistanceRemaining;
    data['legDurationRemaining'] = this.legDurationRemaining;
    data['stepDistanceRemaining'] = this.stepDistanceRemaining;
    data['voiceInstruction'] = this.voiceInstruction;
    data['bannerInstruction'] = this.bannerInstruction;
    data['upComingVoiceInstruction'] = this.upComingVoiceInstruction;
    data['upComingBannerInstruction'] = this.upComingBannerInstruction;
    data['legIndex'] = this.legIndex;
    data['stepIndex'] = this.stepIndex;
    data['currentStepBearingAfter'] = this.currentStepBearingAfter;
    data['currentStepBearingBefore'] = this.currentStepBearingBefore;
    data['currentStepDrivingSide'] = this.currentStepDrivingSide;
    data['currentStepExits'] = this.currentStepExits;
    data['currentStepDistance'] = this.currentStepDistance;
    data['currentStepDuration'] = this.currentStepDuration;
    data['currentStepName'] = this.currentStepName;
    data['upComingStepBearingAfter'] = this.upComingStepBearingAfter;
    data['upComingStepBearingBefore'] = this.upComingStepBearingBefore;
    data['upComingStepDrivingSide'] = this.upComingStepDrivingSide;
    data['upComingStepExits'] = this.upComingStepExits;
    data['upComingStepDistance'] = this.upComingStepDistance;
    data['upComingStepDuration'] = this.upComingStepDuration;
    data['upComingStepName'] = this.upComingStepName;
    return data;
  }
}