# Lexica: The Android Word Game

[![Build Status](https://travis-ci.org/lexica/lexica.svg?branch=master)](https://travis-ci.org/lexica/lexica) <a href="https://hosted.weblate.org/engage/lexica/?utm_source=widget">
  <img src="https://hosted.weblate.org/widgets/lexica/-/svg-badge.svg" alt="Translation status" />
</a>

<a href="https://f-droid.org/repository/browse/?fdid=com.serwylo.lexica">
  <img src="https://f-droid.org/wiki/images/5/55/F-Droid-button_get-it-on_bigger.png" alt="Available on F-Droid" width="160px" />
</a> <a href="https://play.google.com/store/apps/details?id=com.serwylo.lexica">
  <img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Available on Google Play" width="160px" />
</a>

Lexica is a word game that is available on the Android platform. Players are 
given 3 to 30 minutes to find as many words as possible on a grid of random letters.

Features:
 * 4x4 and 5x5 game boards
 * A dictionary of 77,000+ words
 * UK and US dictionaries

## Contributing

### Reporting Issues

Please report any issues or suggest features on the [issue tracker](https://github.com/lexica/lexica/issues).

### Translating

We use [Weblate](https://hosted.weblate.org/engage/lexica/) to manage translations. Please see [these instructions for using Weblate](https://hosted.weblate.org/engage/lexica/) to translate Lexica.

### Submitting changes

Pull requests will be warmly received at [https://github.com/lexica/lexica](https://github.com/lexica/lexica).

## Compiling

This app uses a typical `gradle` folder structure.

 * To build (a debug version): `gradle assembleDebug`
 * To run tests: `gradle test`

Alternatively, you can import the project into Android Studio and build from there.
The tests can also be run from Android Studio, except some will fail when run from the IDE due to [this bug](https://code.google.com/p/android/issues/detail?id=64887).

## Differences from original

This fork is based on the [original Lexic](http://code.google.com/p/lexic).

So far, differences include:
 * Removed unsupported multiplayer options
 * Better support for screens with higher resolutions
 * Action bar support
 * Minor Tweaks to the UI
