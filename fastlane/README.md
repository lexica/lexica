Fastlane is used to:

* Automatically screenshot the app in supported languages.
* Upload metadata to Google Play.

It is *not* used to build the app, because we let F-Droid do that in order to manage signing keys.

## How to take screenshots

**Build and install the test apk on an Android 27 emulator**

```
./gradlew clean installDebugAndroidTest
```

Androi 27 is required because the way the test navigates the preferences menu and presses the "Up" button
depends quite heavily on the specific API being used unfortunately. Furthermore, at the time of writing
the latest version of Android (29) did not work well with Fastlane Screengrab, hence Androi 27.

**Take screenshots**

`bundle exec fastlane screengrab`

After running, it will output the path to a .html file which summarises all of the screenshots quite nicely.
Have a look at this, because it seems that some locales often incorrectly pull the wrong screenshots from the phone.
In such a case, manually take a backup of all the successful screenshots, comment out all other locales, and run
`fastlane screengrab` again to regenerate the locale(s) of interest.
