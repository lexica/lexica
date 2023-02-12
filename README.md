# Lexica: The Android Word Game

[![Liberapay receiving](https://img.shields.io/liberapay/receives/Lexica)](https://liberapay.com/Lexica/donate)
[![Translation status](https://hosted.weblate.org/widgets/lexica/-/svg-badge.svg)](https://hosted.weblate.org/engage/lexica/)
[![F-Droid version](https://img.shields.io/f-droid/v/com.serwylo.lexica)](https://f-droid.org/packages/com.serwylo.lexica/)
[![Build Status](https://img.shields.io/github/workflow/status/lexica/lexica/Android%20CI)](https://github.com/lexica/lexica/actions/workflows/android.yml?query=branch%3Amaster)

[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80px">](https://f-droid.org/app/com.serwylo.lexica) [<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height='80px'/>](https://play.google.com/store/apps/details?id=com.serwylo.lexica)

Lexica is a word game that is available on the Android platform. Players are 
given 3 to 30 minutes to find as many words as possible on a grid of random letters.

Features:
 * Several international dictionaries, with a combined total of millions of words
 * Multiplayer mode (send challenges to friends via SMS/Email/etc)
 * Customisable game modes:
   * 4x4, 5x5, and 6x6 sized boards
   * Different durations
   * Various scoring modes

## Screenshots

<img src="./fastlane/metadata/android/en-US/images/phoneScreenshots/01_main_menu_light.png" alt="Lexica game main menu" width="200"> <img src="./fastlane/metadata/android/en-US/images/phoneScreenshots/02_game_light.png" alt="In game screenshot (light theme)" width="200"> <img src="./fastlane/metadata/android/en-US/images/phoneScreenshots/03_missed_words_light.png" alt="End game screen (showing missed words)" width="200"> <img src="./fastlane/metadata/android/en-US/images/phoneScreenshots/06_game_dark.png" alt="In game screenshot (dark theme)" width="200">

## How to play the game

The user can click on the any letter in the grid.
The user can slide his finger to choose the next letter of the word that the user wants to find.
The user may move to all 8 neighboring letters from the original letter; 2 horizontally neighboring letters, 2 vertically neighboring letters, 4 diagnoally neighboring letters.
The user can complete the sequence of letters by moving his finger off the screen after selecting the letters.
In the given time, the user can find as many words as possible. 
Each word found will have a different point in relation to the length of that word.
For example, a letter with the length of 3 or 4 will be 1 point. A letter with the length of 5 will be 2 points.
There are different game modes including Sprint, Marathon, Beginner and Letter Points.
In Sprint mode, the player is given alloted time.
In Marathon mode, the player does not have time pressure.
In Beginner mode, the player can use hints to find words.
In Letter Points mode, the player can score more points for using less common words.
Before beginning any game, the player can switch game mode on clicking trophy logo on the left top.
After the alloted time expires, the game will display how many words you found out of how many words there were in the grid.
Besides the words you have found, it will display how many points you have earned out of how many points were possible to be gained.
The user can end the game earlier in the middle of the game if the user wishes to by clicking the three dots. After clicking the three dots, the user will be prompted with "end game" button. 
There will be two modes after completion of the game: Found words and Missed words.
The "Found Words" mode will be the default mode after completion of the game.
It will display the words you have found in the list.
It will also display the combination of letters that aren't any words that you have tried during the game.
The each word you found will have points earend beside the word.
The each word found will have an external link next to the point that will direct the players to web page of the definition of each word.
In the Missed Words mode, it will display the original grid.
It will also list each word that weren't found during the game, along with their alloted points and link to the web page of the definition of each word.
When the player click on the each word missed, the grid will display the path of the word.
The player can sort the missed word alphabetically.
The player can click on three dots connected in a line to share his game result.
The player can share the game result to text message, via whatsapp or via email.

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
