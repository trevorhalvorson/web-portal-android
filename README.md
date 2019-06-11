# Portal

A simple WebView application with configurations set by build parameters.
Lock down a device to only display your website.

### Building

Project Properties:

- `applicationId`: Package name of the application
- `versionCode`: Version code of the application. This must be incremented to perform app updates
- `versionName`: Version name of the application.
- `url`: The URL to load in the WebView
- `allowBrowsing`: `true` if the user should be allowed to access hosts other than the given `url`'s host
- `allowFileUpload`: `true` if the user should be allowed to access and upload files on the device through the WebView
- `toolbarTitle`: Text shown in the toolbar above the WebView

Build a signed release APK:

`KEYSTORE=keystore.jks KEYSTORE_PASSWORD=password KEY_ALIAS=key0 KEY_PASSWORD=password ./gradlew assembleRelease -PapplicationId='com.trevorhalvorson.portal' -PversionCode=1 -PversionName='1.0.0' -Purl='"https://www.wikipedia.org/"' -PallowBrowsing=true -PallowFileUpload=false -PtoolbarTitle='"Portal"'`

### Self-Signing

Generating a Keystore:

`keytool -genkey -v -dname "cn=testcn, ou=testou, o=testo, c=US" -alias testalias -keypass testkeypass -keystore test.keystore -storepass teststorepass -keyalg RSA -keysize 2048 -validity 999`

Signing an APK with [apksigner](https://developer.android.com/studio/command-line/apksigner.html):

`apksigner sign --ks test.keystore --in app/build/outputs/apk/release/app-release-unsigned.apk --out app-signed.apk`

Verify signing:

`apksigner verify -v --print-certs app-signed.apk`
