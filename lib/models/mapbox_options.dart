class MapBoxOptions {
  double initialLat;
  double initialLong;
  bool shouldSimulateRoute;
  String language;
  double zoom;
  double bearing;
  double tilt;
  bool alternatives;
  String clientAppName;
  String profile;
  bool continueStraight;
  bool enableRefresh;
  bool steps;
  bool voiceInstructions;
  bool bannerInstructions;
  String testRoute;
  bool debug;

  MapBoxOptions(
      {this.initialLat,
      this.initialLong,
      this.shouldSimulateRoute,
      this.language,
      this.zoom,
      this.bearing,
      this.tilt,
      this.alternatives,
      this.clientAppName,
      this.profile,
      this.continueStraight,
      this.enableRefresh,
      this.steps,
      this.voiceInstructions,
      this.bannerInstructions,
      this.testRoute,
      this.debug});

  MapBoxOptions.fromJson(Map<String, dynamic> json) {
    initialLat = json['initialLat'];
    initialLong = json['initialLong'];
    shouldSimulateRoute = json['shouldSimulateRoute'];
    language = json['language'];
    zoom = json['zoom'];
    bearing = json['bearing'];
    tilt = json['tilt'];
    alternatives = json['alternatives'];
    clientAppName = json['clientAppName'];
    profile = json['profile'];
    continueStraight = json['continueStraight'];
    enableRefresh = json['enableRefresh'];
    steps = json['steps'];
    voiceInstructions = json['voiceInstructions'];
    bannerInstructions = json['bannerInstructions'];
    testRoute = json['testRoute'];
    debug = json['debug'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['initialLat'] = this.initialLat;
    data['initialLong'] = this.initialLong;
    data['shouldSimulateRoute'] = this.shouldSimulateRoute;
    data['language'] = this.language;
    data['zoom'] = this.zoom;
    data['bearing'] = this.bearing;
    data['tilt'] = this.tilt;
    data['alternatives'] = this.alternatives;
    data['clientAppName'] = this.clientAppName;
    data['profile'] = this.profile;
    data['continueStraight'] = this.continueStraight;
    data['enableRefresh'] = this.enableRefresh;
    data['steps'] = this.steps;
    data['voiceInstructions'] = this.voiceInstructions;
    data['bannerInstructions'] = this.bannerInstructions;
    data['testRoute'] = this.testRoute;
    data['debug'] = this.debug;
    return data;
  }
}
