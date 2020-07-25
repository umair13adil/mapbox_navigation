import 'dart:convert';

class MapBoxEventResults {
  String eventName;
  String data;

  MapBoxEventResults({this.eventName, this.data});

  MapBoxEventResults.fromJson(Map<String, dynamic> json) {
    eventName = json['eventName'];
    data = jsonEncode(json['data']);
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['eventName'] = this.eventName;
    data['data'] = this.data;
    return data;
  }
}
