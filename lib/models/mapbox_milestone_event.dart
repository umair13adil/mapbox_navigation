class MapBoxMileStoneEvent {
  String identifier;
  String distanceTraveled;
  String legIndex;
  String stepIndex;

  MapBoxMileStoneEvent(
      {this.identifier, this.distanceTraveled, this.legIndex, this.stepIndex});

  MapBoxMileStoneEvent.fromJson(Map<String, dynamic> json) {
    identifier = json['identifier'];
    distanceTraveled = json['distanceTraveled'];
    legIndex = json['legIndex'];
    stepIndex = json['stepIndex'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['identifier'] = this.identifier;
    data['distanceTraveled'] = this.distanceTraveled;
    data['legIndex'] = this.legIndex;
    data['stepIndex'] = this.stepIndex;
    return data;
  }
}