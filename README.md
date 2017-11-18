# Portal

A simple webview application that allows the options to be
set by build parameters.

### Building

Project Properties:

- `url`: The URL to load in the WebView
- `allowBrowsing`: `true` if the user should be allowed to access hosts other than the given `url`'s host
- `versionCode`: The Android App's version. This must be incremented to perform app updates

Build an unsigned release APK:

`./gradlew assembleRelease -Purl='"https://trevorhalvorson.com"' -PallowBrowsing=true -PversionCode=1`
