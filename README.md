# Lexica: The Android Word Game

[![Liberapay receiving](https://img.shields.io/liberapay/receives/Lexica)](https://liberapay.com/Lexica/donate)
[![Translation status](https://hosted.weblate.org/widgets/lexica/-/svg-badge.svg)](https://hosted.weblate.org/engage/lexica/)
[![F-Droid version](https://img.shields.io/f-droid/v/com.serwylo.lexica)](https://f-droid.org/packages/com.serwylo.lexica/)
[![Build Status](https://img.shields.io/github/workflow/status/lexica/lexica/Android%20CI)](https://github.com/lexica/lexica/actions/workflows/android.yml?query=branch%3Amaster)

[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80px">](https://f-droid.org/app/com.serwylo.lexica) [<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height='80px'/>](https://play.google.com/store/apps/details?id=com.serwylo.lexica)

Lexica is a word game that is available on the Android platform. Players are 
given 3 to 30 minutes to find as many words as possible on a grid of random letters.

Features:
 * 4x4, 5x5, and 6x6 game boards
 * Several international dictionaries, with a combined total of millions of words

## Contributing

### Support

Lexica is an open source, GPLv3 game. It will always be freely available via F-Droid, or for anyone to build, fork, or improve via the source code.

If you wish to support further development, you can do so via:

* [Liberapay](https://liberapay.com/Lexica/donate)
* [GitHub sponsors](https://github.com/sponsors/pserwylo)

### Reporting Issues

Please report any issues or suggest features on the [issue tracker](https://github.com/lexica/lexica/issues).

### Translating

We use [Weblate](https://hosted.weblate.org/engage/lexica/) to manage translations. Please see [these instructions for using Weblate](https://hosted.weblate.org/engage/lexica/) to translate Lexica.

### Submitting changes

Pull requests will be warmly received at [https://github.com/lexica/lexica](https://github.com/lexica/lexica).

#### Add a new language

Please read the [documentation for adding a new language.](./assets/dictionaries/README.md)

## Compiling

This app uses a typical `gradle` folder structure.

 * To build (a debug version): `gradle assembleDebug`
 * To run tests: `gradle test`

Alternatively, you can import the project into Android Studio and build/run tests from there.

## Differences from original

This fork is based on the [original Lexic](http://code.google.com/p/lexic).

Some differences include:
 * Multiple international dictionaries and an internationalised UI.
 * New scoring modes, hint modes, and other options.
 * New user interface, including multiple themes.

## Lexica API

A lot of work from many people in the community has gone into producing localised dictionaries
for Lexica. To share this so that you can make your own word games, or your own Lexica versions,
this information is published at:

https://lexica.github.io/lexica/
