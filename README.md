# Portal

A simple webview application that allows the options to be
set by build parameters.

### Building

Project Properties:

- `url`: The URL to load in the WebView
- `allowBrowsing`: `true` if the user should be allowed to access hosts other than the given `url`'s host
- `versionCode`: The Android App's version. This must be incremented to perform app updates

Build an unsigned release APK:

`./gradlew assembleRelease -Purl='"https://trevorhalvorson.com"' -PallowBrowsing=true -PversionCode=1 -PversionName='1.0.0'`

Build a signed release APK:

`KEYSTORE=keystore.jks KEYSTORE_PASSWORD=password KEY_ALIAS=key0 KEY_PASSWORD=password ./gradlew assembleRelease -Purl='"https://trevorhalvorson.com"' -PallowBrowsing=true PversionCode=1 -PversionName='1.0.0'`

### Signing

(optional) Generating a Keystore:

`keytool -genkey -v -dname "cn=testcn, ou=testou, o=testo, c=US" -alias testalias -keypass testkeypass -keystore test.keystore -storepass teststorepass -keyalg RSA -keysize 2048 -validity 999`

Sign APK with [apksigner](https://developer.android.com/studio/command-line/apksigner.html):

`apksigner sign --ks test.keystore --in app/build/outputs/apk/release/app-release-unsigned.apk --out app-signed.apk`

(optional) Verify signing:

`apksigner verify -v --print-certs app-signed.apk`
