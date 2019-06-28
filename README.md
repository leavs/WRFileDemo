# WRFileDemo
Write/Create and Read file, include from EXT Storage(USB Storage or TF Card). need add signature for APK.

We use follow way to access Android6.0 portable storage.

# Modify  AndroidMainfests.xml
add `android:sharedUserId="android.uid.system"` to AndroidMainfest.xml, uses-permission is not need.
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.chipsee.wrfiledemo"
    android:sharedUserId="android.uid.system">

    <uses-permission-sdk-23 android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission-sdk-23 android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
# Add signatures for APK
Get signatures tools from Chipsee, and add signatures for APK like follow:
```
java -jar signapk.jar platform.x509.pem platform.pk8 app-debug.apk new.apk
```
# Install APK
```
adb install new.apk
```
