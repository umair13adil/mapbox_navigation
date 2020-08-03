class MapBoxLocation {
  double latitude;
  double longitude;

  MapBoxLocation(this.latitude, this.longitude);

  MapBoxLocation.fromJson(Map<String, dynamic> json) {
    latitude = json['latitude'];
    longitude = json['longitude'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    return data;
  }

  @override
  String toString() {
    return 'MapBoxLocation{latitude: $latitude, longitude: $longitude}';
  }
}
