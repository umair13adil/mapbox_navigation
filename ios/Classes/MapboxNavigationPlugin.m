#import "MapboxNavigationPlugin.h"
#if __has_include(<mapbox_navigation/mapbox_navigation-Swift.h>)
#import <mapbox_navigation/mapbox_navigation-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mapbox_navigation-Swift.h"
#endif

@implementation MapboxNavigationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMapboxNavigationPlugin registerWithRegistrar:registrar];
}
@end
