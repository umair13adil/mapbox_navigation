import 'mapbox_events.dart';

class MapBoxEventProvider {
  static MapBoxEvent getEventType(String event) {
    if (event == _getEnumValue(MapBoxEvent.map_ready)) {
      return MapBoxEvent.map_ready;
    } else if (event == _getEnumValue(MapBoxEvent.route_built)) {
      return MapBoxEvent.route_built;
    } else if (event == _getEnumValue(MapBoxEvent.progress_change)) {
      return MapBoxEvent.progress_change;
    } else if (event == _getEnumValue(MapBoxEvent.user_off_route)) {
      return MapBoxEvent.user_off_route;
    } else if (event == _getEnumValue(MapBoxEvent.milestone_event)) {
      return MapBoxEvent.milestone_event;
    } else if (event == _getEnumValue(MapBoxEvent.navigation_running)) {
      return MapBoxEvent.navigation_running;
    } else if (event == _getEnumValue(MapBoxEvent.navigation_cancelled)) {
      return MapBoxEvent.navigation_cancelled;
    } else if (event == _getEnumValue(MapBoxEvent.navigation_finished)) {
      return MapBoxEvent.navigation_finished;
    } else if (event == _getEnumValue(MapBoxEvent.faster_route_found)) {
      return MapBoxEvent.faster_route_found;
    } else if (event == _getEnumValue(MapBoxEvent.speech_announcement)) {
      return MapBoxEvent.speech_announcement;
    } else if (event == _getEnumValue(MapBoxEvent.banner_instruction)) {
      return MapBoxEvent.banner_instruction;
    } else if (event == _getEnumValue(MapBoxEvent.on_arrival)) {
      return MapBoxEvent.on_arrival;
    } else if (event == _getEnumValue(MapBoxEvent.failed_to_reroute)) {
      return MapBoxEvent.failed_to_reroute;
    } else if (event == _getEnumValue(MapBoxEvent.reroute_along)) {
      return MapBoxEvent.reroute_along;
    }
  }
}

String _getEnumValue(MapBoxEvent event) {
  return event.toString().split('.').last;
}
