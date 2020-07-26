class MapBoxRouteResponse {
  String code;
  List<Waypoints> waypoints;
  List<Routes> routes;
  String uuid;

  MapBoxRouteResponse({this.code, this.waypoints, this.routes, this.uuid});

  MapBoxRouteResponse.fromJson(Map<String, dynamic> json) {
    code = json['code'];
    if (json['waypoints'] != null) {
      waypoints = new List<Waypoints>();
      json['waypoints'].forEach((v) {
        waypoints.add(new Waypoints.fromJson(v));
      });
    }
    if (json['routes'] != null) {
      routes = new List<Routes>();
      json['routes'].forEach((v) {
        routes.add(new Routes.fromJson(v));
      });
    }
    uuid = json['uuid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['code'] = this.code;
    if (this.waypoints != null) {
      data['waypoints'] = this.waypoints.map((v) => v.toJson()).toList();
    }
    if (this.routes != null) {
      data['routes'] = this.routes.map((v) => v.toJson()).toList();
    }
    data['uuid'] = this.uuid;
    return data;
  }
}

class Waypoints {
  String name;
  List<double> location;

  Waypoints({this.name, this.location});

  Waypoints.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    location = json['location'].cast<double>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['location'] = this.location;
    return data;
  }
}

class Routes {
  String routeIndex;
  double distance;
  double duration;
  String geometry;
  double weight;
  String weightName;
  List<Legs> legs;
  RouteOptions routeOptions;
  String voiceLocale;

  Routes(
      {this.routeIndex,
      this.distance,
      this.duration,
      this.geometry,
      this.weight,
      this.weightName,
      this.legs,
      this.routeOptions,
      this.voiceLocale});

  Routes.fromJson(Map<String, dynamic> json) {
    routeIndex = json['routeIndex'];
    distance = json['distance'];
    duration = json['duration'];
    geometry = json['geometry'];
    weight = json['weight'];
    weightName = json['weight_name'];
    if (json['legs'] != null) {
      legs = new List<Legs>();
      json['legs'].forEach((v) {
        legs.add(new Legs.fromJson(v));
      });
    }
    routeOptions = json['routeOptions'] != null
        ? new RouteOptions.fromJson(json['routeOptions'])
        : null;
    voiceLocale = json['voiceLocale'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['routeIndex'] = this.routeIndex;
    data['distance'] = this.distance;
    data['duration'] = this.duration;
    data['geometry'] = this.geometry;
    data['weight'] = this.weight;
    data['weight_name'] = this.weightName;
    if (this.legs != null) {
      data['legs'] = this.legs.map((v) => v.toJson()).toList();
    }
    if (this.routeOptions != null) {
      data['routeOptions'] = this.routeOptions.toJson();
    }
    data['voiceLocale'] = this.voiceLocale;
    return data;
  }
}

class Legs {
  double distance;
  double duration;
  String summary;
  List<Steps> steps;
  Annotation annotation;

  Legs(
      {this.distance,
      this.duration,
      this.summary,
      this.steps,
      this.annotation});

  Legs.fromJson(Map<String, dynamic> json) {
    distance = json['distance'];
    duration = json['duration'];
    summary = json['summary'];
    if (json['steps'] != null) {
      steps = new List<Steps>();
      json['steps'].forEach((v) {
        steps.add(new Steps.fromJson(v));
      });
    }
    annotation = json['annotation'] != null
        ? new Annotation.fromJson(json['annotation'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distance'] = this.distance;
    data['duration'] = this.duration;
    data['summary'] = this.summary;
    if (this.steps != null) {
      data['steps'] = this.steps.map((v) => v.toJson()).toList();
    }
    if (this.annotation != null) {
      data['annotation'] = this.annotation.toJson();
    }
    return data;
  }
}

class Steps {
  double distance;
  double duration;
  String geometry;
  String name;
  String mode;
  Maneuver maneuver;
  List<VoiceInstructions> voiceInstructions;
  List<BannerInstructions> bannerInstructions;
  String drivingSide;
  double weight;
  List<Intersections> intersections;
  String ref;

  Steps(
      {this.distance,
      this.duration,
      this.geometry,
      this.name,
      this.mode,
      this.maneuver,
      this.voiceInstructions,
      this.bannerInstructions,
      this.drivingSide,
      this.weight,
      this.intersections,
      this.ref});

  Steps.fromJson(Map<String, dynamic> json) {
    distance = json['distance'];
    duration = json['duration'];
    geometry = json['geometry'];
    name = json['name'];
    mode = json['mode'];
    maneuver = json['maneuver'] != null
        ? new Maneuver.fromJson(json['maneuver'])
        : null;
    if (json['voiceInstructions'] != null) {
      voiceInstructions = new List<VoiceInstructions>();
      json['voiceInstructions'].forEach((v) {
        voiceInstructions.add(new VoiceInstructions.fromJson(v));
      });
    }
    if (json['bannerInstructions'] != null) {
      bannerInstructions = new List<BannerInstructions>();
      json['bannerInstructions'].forEach((v) {
        bannerInstructions.add(new BannerInstructions.fromJson(v));
      });
    }
    drivingSide = json['driving_side'];
    weight = json['weight'];
    if (json['intersections'] != null) {
      intersections = new List<Intersections>();
      json['intersections'].forEach((v) {
        intersections.add(new Intersections.fromJson(v));
      });
    }
    ref = json['ref'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distance'] = this.distance;
    data['duration'] = this.duration;
    data['geometry'] = this.geometry;
    data['name'] = this.name;
    data['mode'] = this.mode;
    if (this.maneuver != null) {
      data['maneuver'] = this.maneuver.toJson();
    }
    if (this.voiceInstructions != null) {
      data['voiceInstructions'] =
          this.voiceInstructions.map((v) => v.toJson()).toList();
    }
    if (this.bannerInstructions != null) {
      data['bannerInstructions'] =
          this.bannerInstructions.map((v) => v.toJson()).toList();
    }
    data['driving_side'] = this.drivingSide;
    data['weight'] = this.weight;
    if (this.intersections != null) {
      data['intersections'] =
          this.intersections.map((v) => v.toJson()).toList();
    }
    data['ref'] = this.ref;
    return data;
  }
}

class Maneuver {
  List<double> location;
  double bearingBefore;
  double bearingAfter;
  String instruction;
  String type;
  String modifier;
  int exit;

  Maneuver(
      {this.location,
      this.bearingBefore,
      this.bearingAfter,
      this.instruction,
      this.type,
      this.modifier,
      this.exit});

  Maneuver.fromJson(Map<String, dynamic> json) {
    location = json['location'].cast<double>();
    bearingBefore = json['bearing_before'] == null
        ? 0.0
        : json['bearing_before'].toDouble();
    bearingAfter =
        json['bearing_after'] == null ? 0.0 : json['bearing_after'].toDouble();
    instruction = json['instruction'];
    type = json['type'];
    modifier = json['modifier'];
    exit = json['exit'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['location'] = this.location;
    data['bearing_before'] = this.bearingBefore;
    data['bearing_after'] = this.bearingAfter;
    data['instruction'] = this.instruction;
    data['type'] = this.type;
    data['modifier'] = this.modifier;
    data['exit'] = this.exit;
    return data;
  }
}

class VoiceInstructions {
  double distanceAlongGeometry;
  String announcement;
  String ssmlAnnouncement;

  VoiceInstructions(
      {this.distanceAlongGeometry, this.announcement, this.ssmlAnnouncement});

  VoiceInstructions.fromJson(Map<String, dynamic> json) {
    distanceAlongGeometry = json['distanceAlongGeometry'];
    announcement = json['announcement'];
    ssmlAnnouncement = json['ssmlAnnouncement'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distanceAlongGeometry'] = this.distanceAlongGeometry;
    data['announcement'] = this.announcement;
    data['ssmlAnnouncement'] = this.ssmlAnnouncement;
    return data;
  }
}

class Primary {
  String text;
  List<Components> components;
  String type;
  String modifier;
  double degrees;
  String drivingSide;

  Primary(
      {this.text,
      this.components,
      this.type,
      this.modifier,
      this.degrees,
      this.drivingSide});

  Primary.fromJson(Map<String, dynamic> json) {
    text = json['text'];
    if (json['components'] != null) {
      components = new List<Components>();
      json['components'].forEach((v) {
        components.add(new Components.fromJson(v));
      });
    }
    type = json['type'];
    modifier = json['modifier'];
    degrees = json['degrees'] == null ? 0.0 : json['degrees'].toDouble();
    drivingSide = json['driving_side'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['text'] = this.text;
    if (this.components != null) {
      data['components'] = this.components.map((v) => v.toJson()).toList();
    }
    data['type'] = this.type;
    data['modifier'] = this.modifier;
    data['degrees'] = this.degrees;
    data['driving_side'] = this.drivingSide;
    return data;
  }
}

class Components {
  String text;
  String type;
  String abbr;
  int abbrPriority;

  Components({this.text, this.type, this.abbr, this.abbrPriority});

  Components.fromJson(Map<String, dynamic> json) {
    text = json['text'];
    type = json['type'];
    abbr = json['abbr'];
    abbrPriority = json['abbr_priority'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['text'] = this.text;
    data['type'] = this.type;
    data['abbr'] = this.abbr;
    data['abbr_priority'] = this.abbrPriority;
    return data;
  }
}

class Secondary {
  String text;
  List<Components> components;
  String type;
  String modifier;

  Secondary({this.text, this.components, this.type, this.modifier});

  Secondary.fromJson(Map<String, dynamic> json) {
    text = json['text'];
    if (json['components'] != null) {
      components = new List<Components>();
      json['components'].forEach((v) {
        components.add(new Components.fromJson(v));
      });
    }
    type = json['type'];
    modifier = json['modifier'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['text'] = this.text;
    if (this.components != null) {
      data['components'] = this.components.map((v) => v.toJson()).toList();
    }
    data['type'] = this.type;
    data['modifier'] = this.modifier;
    return data;
  }
}

class Intersections {
  List<double> location;
  List<double> bearings;
  List<bool> entry;
  int outValue;
  int inValue;
  List<String> classes;

  Intersections(
      {this.location,
      this.bearings,
      this.entry,
      this.outValue,
      this.inValue,
      this.classes});

  Intersections.fromJson(Map<String, dynamic> json) {
    location = json['location'].cast<double>();
    bearings = json['bearings'].cast<double>();
    entry = json['entry'].cast<bool>();
    outValue = json['out'];
    inValue = json['in'];
    if (json['classes'] != null) classes = json['classes'].cast<String>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['location'] = this.location;
    data['bearings'] = this.bearings;
    data['entry'] = this.entry;
    data['out'] = this.outValue;
    data['in'] = this.inValue;
    if (this.classes != null) data['classes'] = this.classes;
    return data;
  }
}

class Annotation {
  List<double> distance;
  List<String> congestion;

  Annotation({this.distance, this.congestion});

  Annotation.fromJson(Map<String, dynamic> json) {
    distance = json['distance'].cast<double>();
    congestion = json['congestion'].cast<String>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distance'] = this.distance;
    data['congestion'] = this.congestion;
    return data;
  }
}

class RouteOptions {
  String baseUrl;
  String user;
  String profile;

  //List<List> coordinates;
  bool alternatives;
  String language;
  String bearings;
  bool continueStraight;
  bool roundaboutExits;
  String geometries;
  String overview;
  bool steps;
  String annotations;
  bool voiceInstructions;
  bool bannerInstructions;
  String voiceUnits;
  String accessToken;
  String uuid;

  RouteOptions(
      {this.baseUrl,
      this.user,
      this.profile,
      //this.coordinates,
      this.alternatives,
      this.language,
      this.bearings,
      this.continueStraight,
      this.roundaboutExits,
      this.geometries,
      this.overview,
      this.steps,
      this.annotations,
      this.voiceInstructions,
      this.bannerInstructions,
      this.voiceUnits,
      this.accessToken,
      this.uuid});

  RouteOptions.fromJson(Map<String, dynamic> json) {
    baseUrl = json['baseUrl'];
    user = json['user'];
    profile = json['profile'];
    /*if (json['coordinates'] != null) {
      coordinates = new List<List>();
      json['coordinates'].forEach((v) {
        coordinates.add(new List.fromJson(v));
      });
    }*/
    alternatives = json['alternatives'];
    language = json['language'];
    bearings = json['bearings'];
    continueStraight = json['continue_straight'];
    roundaboutExits = json['roundabout_exits'];
    geometries = json['geometries'];
    overview = json['overview'];
    steps = json['steps'];
    annotations = json['annotations'];
    voiceInstructions = json['voice_instructions'];
    bannerInstructions = json['banner_instructions'];
    voiceUnits = json['voice_units'];
    accessToken = json['access_token'];
    uuid = json['uuid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['baseUrl'] = this.baseUrl;
    data['user'] = this.user;
    data['profile'] = this.profile;
    /*if (this.coordinates != null) {
      data['coordinates'] = this.coordinates.map((v) => v.toJson()).toList();
    }*/
    data['alternatives'] = this.alternatives;
    data['language'] = this.language;
    data['bearings'] = this.bearings;
    data['continue_straight'] = this.continueStraight;
    data['roundabout_exits'] = this.roundaboutExits;
    data['geometries'] = this.geometries;
    data['overview'] = this.overview;
    data['steps'] = this.steps;
    data['annotations'] = this.annotations;
    data['voice_instructions'] = this.voiceInstructions;
    data['banner_instructions'] = this.bannerInstructions;
    data['voice_units'] = this.voiceUnits;
    data['access_token'] = this.accessToken;
    data['uuid'] = this.uuid;
    return data;
  }
}

class BannerInstructions {
  double distanceAlongGeometry;
  Primary primary;
  Primary sub;
  Secondary secondary;

  BannerInstructions(
      {this.distanceAlongGeometry, this.primary, this.sub, this.secondary});

  BannerInstructions.fromJson(Map<String, dynamic> json) {
    distanceAlongGeometry = json['distanceAlongGeometry'];
    primary =
        json['primary'] != null ? new Primary.fromJson(json['primary']) : null;
    sub = json['sub'] != null ? new Primary.fromJson(json['sub']) : null;
    secondary = json['secondary'] != null
        ? new Secondary.fromJson(json['secondary'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['distanceAlongGeometry'] = this.distanceAlongGeometry;
    if (this.primary != null) {
      data['primary'] = this.primary.toJson();
    }
    if (this.sub != null) {
      data['sub'] = this.sub.toJson();
    }
    if (this.secondary != null) {
      data['secondary'] = this.secondary.toJson();
    }
    return data;
  }
}
