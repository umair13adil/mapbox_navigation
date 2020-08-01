class MapBoxProgressEvent {
  String distance;
  String duration;
  String distanceTraveled;
  String legDistanceTraveled;
  String legDistanceRemaining;
  String legDurationRemaining;
  String voiceInstruction;
  String bannerInstruction;
  String legIndex;
  String stepIndex;

  MapBoxProgressEvent(
      {this.distance,
        this.duration,
        this.distanceTraveled,
        this.legDistanceTraveled,
        this.legDistanceRemaining,
        this.legDurationRemaining,
        this.voiceInstruction,
        this.bannerInstruction,
        this.legIndex,
        this.stepIndex});

  MapBoxProgressEvent.fromJson(Map<String, dynamic> json) {
    distance = json['distance'];
    duration = json['duration'];
    distanceTraveled = json['distanceTraveled'];
    legDistanceTraveled = json['legDistanceTraveled'];
    legDistanceRemaining = json['legDistanceRemaining'];
    legDurationRemaining = json['legDurationRemaining'];
    voiceInstruction = json['voiceInstruction'];
    bannerInstruction = json['bannerInstruction'];
    legIndex = json['legIndex'];
    stepIndex = json['stepIndex'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distance'] = this.distance;
    data['duration'] = this.duration;
    data['distanceTraveled'] = this.distanceTraveled;
    data['legDistanceTraveled'] = this.legDistanceTraveled;
    data['legDistanceRemaining'] = this.legDistanceRemaining;
    data['legDurationRemaining'] = this.legDurationRemaining;
    data['voiceInstruction'] = this.voiceInstruction;
    data['bannerInstruction'] = this.bannerInstruction;
    data['legIndex'] = this.legIndex;
    data['stepIndex'] = this.stepIndex;
    return data;
  }
}