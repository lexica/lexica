# Lexica: The Android Word Game

[<img alt="Donate using Liberapay" src="https://liberapay.com/assets/widgets/donate.svg">](https://liberapay.com/Lexica/donate)

[![Build Status](https://travis-ci.org/lexica/lexica.svg?branch=master)](https://travis-ci.org/lexica/lexica) <a href="https://hosted.weblate.org/engage/lexica/?utm_source=widget">
  <img src="https://hosted.weblate.org/widgets/lexica/-/svg-badge.svg" alt="Translation status" />
</a>

[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80px">](https://f-droid.org/app/com.serwylo.lexica) [<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height='80px'/>](https://play.google.com/store/apps/details?id=com.serwylo.lexica)

Lexica is a word game that is available on the Android platform. Players are 
given 3 to 30 minutes to find as many words as possible on a grid of random letters.

Features:
 * 4x4, 5x5, and 6x6 game boards
 * Several international dictionaries, with a combined total of millions of words

## Contributing

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
